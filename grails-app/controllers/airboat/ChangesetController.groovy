package airboat

import grails.converters.JSON
import static com.google.common.base.Strings.isNullOrEmpty
import org.springframework.security.access.AccessDeniedException

class ChangesetController {

    ScmAccessService scmAccessService
    ReturnCommentsService returnCommentsService
    MyCommentsAndChangesetsFilterService myCommentsAndChangesetsFilterService
    CommentedChangesetsFilterService commentedChangesetsFilterService

    def filterTypes = ['commentedChangesets', 'myCommentsAndChangesets']

    def index() {
        def projectName = params.projectName
        def changesetId = params.changesetId
        def filter = params.filter
        if (projectName != null && changesetId != null) {
            renderChangesetResponse(projectName, changesetId)
        } else if (projectName != null) {
            renderProjectResponse(projectName)
        } else if (filter != null) {
            renderFilterResponse(filter)
        } else {
            render(view: 'index', model: [projects: Project.all.sort{Project it -> it.name}, type: 'project', singleProject: false])
        }
    }

    def getLastChangesets(String projectName) {
        def changesets
        if (isNullOrEmpty(projectName)) {
            changesets = Changeset.list(max: Constants.FIRST_CHANGESET_LOAD_SIZE, sort: 'date', order: 'desc')
        } else {
            changesets = getLastChagesetsFromProject(projectName)
        }
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render ([changesets: changesetsProperties, isImporting: isImporting(projectName)] as JSON)
    }

    def getLastFilteredChangesets(String filterType) {
        def filterServices = [commentedChangesets: commentedChangesetsFilterService, myCommentsAndChangesets: myCommentsAndChangesetsFilterService]
        def changesets
        try {
            changesets = filterServices.get(filterType).getLastFilteredChangesets()
        } catch (AccessDeniedException e) {
            response.sendError(401)
            return
        }
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render ([changesets: changesetsProperties, isImporting: isImporting()] as JSON)
    }

    private List<Changeset> getLastChagesetsFromProject(String projectName) {
        //TODO examine number of queries and try to make it 1.
        def project = Project.findByName(projectName)
        Changeset.findAllByProject(project, [max: Constants.FIRST_CHANGESET_LOAD_SIZE, sort: 'date', order: 'desc'])
    }

    def getNextFewChangesetsOlderThan(Long changesetId) {
        def changesetsProperties = getChangesetsGroups(getNextFewChangesetsFromAllProjects(changesetId))
        render ([changesets: changesetsProperties, isImporting: isImporting()] as JSON)
    }

    def getNextFewChangesetsOlderThanFromSameProject(Long changesetId) {
        def projectName = Changeset.findById(changesetId).project.name
        def changesetsProperties = getChangesetsGroups(getNextFewChangesetsFromSameProject(changesetId))
        render ([changesets: changesetsProperties, isImporting: isImporting(projectName)] as JSON)
    }

    def getNextFewFilteredChangesetsOlderThan(Long changesetId, String filterType) {
        def filterServices = [commentedChangesets: commentedChangesetsFilterService, myCommentsAndChangesets: myCommentsAndChangesetsFilterService]
        def changesetsProperties = getChangesetsGroups(filterServices.get(filterType).getNextFilteredChangesets(changesetId))
        render ([changesets: changesetsProperties, isImporting: isImporting()] as JSON)
    }

    private def getChangesetsGroups(List<Changeset> nextFewChangesets) {
        def changesetsProperties = nextFewChangesets.collect this.&convertToChangesetProperties
        return groupChangesetPropertiesByDay(changesetsProperties)
    }

    private def groupChangesetPropertiesByDay(changesetsProperties) {
        return changesetsProperties.groupBy { it.date.substring(0, 10) }
    }

    private List<Changeset> getNextFewChangesetsFromAllProjects(Long changesetId) {
        Changeset.where {
            date < property(date).of { id == changesetId }
        }.list(max: Constants.NEXT_CHANGESET_LOAD_SIZE, sort: 'date', order: 'desc')
    }

    private List<Changeset> getNextFewChangesetsFromSameProject(Long changesetId) {
        Changeset.withCriteria {
            lt 'date', {
                eq "id", changesetId
                projections {
                    property "date"
                }
            }
            eq 'project', {
                eq "id", changesetId
                projections {
                    property "project"
                }
            }
            maxResults(Constants.NEXT_CHANGESET_LOAD_SIZE)
            order('date', 'desc')
        }
    }

    private def convertToChangesetProperties(Changeset changeset) {
        [
                id: changeset.id,
                identifier: changeset.identifier,
                author: changeset.commiter.cvsCommiterId,
                email: getEmail(changeset.commiter),
                date: changeset.date.format('yyyy-MM-dd HH:mm'),
                commitMessage: changeset.commitMessage,
                commentsCount: changeset.commentsCount,
                projectName: changeset.project.name,
                allComments: allCommentsCount(changeset),
                projectFiles: getChangesetFiles(changeset),
                comments: returnCommentsToChangeset(changeset.identifier)
        ]
    }

    private String getEmail(Commiter commiter) {
        commiter.user?.email ?: commiter.email
    }

    private int allCommentsCount(Changeset changeset) {
        def allLineComments = ThreadPositionInFile.executeQuery(
                "select position.thread.comments from ThreadPositionInFile as position \
                where position.projectFileInChangeset.changeset = :changeset",
                [changeset: changeset]
        )
        return (allLineComments.size() + changeset.commentsCount)
    }

    private List<Map> getChangesetFiles(Changeset changeset) {
        def files = changeset.projectFilesInChangeset?.sort { it.projectFile.name }
        def fileProperties = files.collect this.&getFileJSONProperties
        fileProperties
    }

    //FIXME adapt front-end to new object structure
    private def getFileJSONProperties(ProjectFileInChangeset projectFileInChangeset) {
        def projectFile = projectFileInChangeset.projectFile
        def projectFileProperties = projectFile.properties + [
                id: projectFile.id,
                commentsCount: projectFileInChangeset.commentThreadsPositions*.thread*.comments?.flatten()?.size(),
                changeType: projectFileInChangeset.changeType
        ]
        projectFileProperties.keySet().retainAll(
                'id', 'name', 'textFormat', 'commentsCount', 'changeType', 'fileType'
        )
        projectFileProperties
    }

    private def returnCommentsToChangeset(String changesetIdentifier) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangeset(changeset)
        def commentsProperties = comments.collect returnCommentsService.&getCommentJSONproperties
        commentsProperties
    }

    private def renderChangesetResponse(projectName, changesetId) {
        def changeset = Changeset.findByIdentifierAndProject(changesetId, Project.findByName(projectName))
        if (changeset != null) {
            def changesetProperties = convertToChangesetProperties(changeset)
            render(view: 'index', model: [projects: Project.all.sort{Project it -> it.name},
                    changeset: groupChangesetPropertiesByDay([changesetProperties]) as JSON,
                    changesetId: changesetId,
                    projectName: projectName,
                    type: 'changeset'])
        } else {
            response.sendError(404, 'Changeset not found')
        }
    }

    private def renderProjectResponse(projectName) {
        if (Project.findByName(projectName) != null) {
            render(view: 'index', model: [projects:Project.all.sort{Project it -> it.name},
                    type: 'project',
                    singleProject: true,
                    projectName: projectName])
        } else {
            response.sendError(404, 'Project not found')
        }
    }

    private def renderFilterResponse(filter) {
        if (filter in filterTypes) {
            render(view: 'index', model: [projects: Project.all.sort{Project it -> it.name},
                    type: 'filter',
                    filterType: filter])
        } else {
            response.sendError(404, 'There is no such filter')
        }
    }

    private def isImporting(projectName = null) {
        if (projectName) {
            return Project.findAllByNameAndStateNotEqual(projectName, Project.ProjectState.fullyImported).size() > 0
        } else {
            return Project.findAllByStateNotEqual(Project.ProjectState.fullyImported).size() > 0
        }
    }
}

