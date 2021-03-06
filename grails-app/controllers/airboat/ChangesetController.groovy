package airboat

import grails.converters.JSON
import static com.google.common.base.Strings.isNullOrEmpty
import org.springframework.security.access.AccessDeniedException

class ChangesetController {

    ScmAccessService scmAccessService
    CommentConverterService commentConverterService
    MyCommentsAndChangesetsFilterService myCommentsAndChangesetsFilterService
    CommentedChangesetsFilterService commentedChangesetsFilterService
    FileFilterService fileFilterService

    def filterTypes = ['commentedChangesets', 'myCommentsAndChangesets', 'fileFilter']

    def index() {
        def projectName = params.projectName
        def changesetId = params.changesetId
        def filter = params.filterType
        if (projectName != null && changesetId != null) {
            renderChangesetResponse(projectName, changesetId)
        } else if (projectName != null) {
            renderProjectResponse(projectName)
        } else if (filter != null) {
            renderFilterResponse(filter, params.additionalInfo)
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
        render ([changesets: changesetsProperties, projectsInImport: projectsInImport()] as JSON)
    }

    def getLastFilteredChangesets(FilterCommand filter) {
        def filterServices = getFilterServiceMap()
        def changesets
        try {
            changesets = filterServices.get(filter.filterType).getLastFilteredChangesets(filter.additionalInfo)
        } catch (AccessDeniedException e) {
            response.sendError(401)
            return
        }
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render ([changesets: changesetsProperties, projectsInImport: projectsInImport()] as JSON)
    }

    private List<Changeset> getLastChagesetsFromProject(String projectName) {
        //TODO examine number of queries and try to make it 1.
        def project = Project.findByName(projectName)
        Changeset.findAllByProject(project, [max: Constants.FIRST_CHANGESET_LOAD_SIZE, sort: 'date', order: 'desc'])
    }

    def getNextFewChangesetsOlderThan(Long changesetId) {
        def changesetsProperties = getChangesetsGroups(getNextFewChangesetsFromAllProjects(changesetId))
        render ([changesets: changesetsProperties, projectsInImport: projectsInImport()] as JSON)
    }

    def getNextFewChangesetsOlderThanFromSameProject(Long changesetId) {
        def changesetsProperties = getChangesetsGroups(getNextFewChangesetsFromSameProject(changesetId))
        render ([changesets: changesetsProperties, projectsInImport: projectsInImport()] as JSON)
    }

    def getNextFewFilteredChangesetsOlderThan(Long changesetId, FilterCommand filter) {
        def filterServices = getFilterServiceMap()
        def changesetsProperties = getChangesetsGroups(filterServices.get(filter.filterType).getNextFilteredChangesets(changesetId, filter.additionalInfo))
        render ([changesets: changesetsProperties, projectsInImport: projectsInImport()] as JSON)
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
                projectName: changeset.project.name,
                projectFiles: getChangesetFiles(changeset),
                comments: returnCommentsToChangeset(changeset.identifier)
        ]
    }

    private String getEmail(Commiter commiter) {
        commiter.user?.email ?: commiter.email
    }

    private List<Map> getChangesetFiles(Changeset changeset) {
        def files = changeset.projectFilesInChangeset?.sort { it.projectFile.name }
        def fileProperties = files.collect this.&getFileJSONProperties
        fileProperties
    }

    private def getFileJSONProperties(ProjectFileInChangeset projectFileInChangeset) {
        def projectFile = projectFileInChangeset.projectFile
        def projectFileProperties = projectFile.properties + [
                id: projectFile.id,
                commentsCount: projectFileInChangeset.commentThreadsPositions*.thread*.comments?.flatten()?.
                        findResults {LineComment comment -> comment.isArchived ? null: comment}?.size(),
                changeType: projectFileInChangeset.changeType
        ]
        projectFileProperties.keySet().retainAll(
                'id', 'name', 'textFormat', 'commentsCount', 'changeType', 'fileType'
        )
        projectFileProperties
    }

    private def returnCommentsToChangeset(String changesetIdentifier) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangesetAndIsArchived(changeset, false)
        def commentsProperties = comments.collect commentConverterService.&getCommentJSONproperties
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

    private def renderFilterResponse(filter, additionalInfo) {
        if (filter in filterTypes) {
            render(view: 'index', model: [projects: Project.all.sort{Project it -> it.name},
                    type: 'filter',
                    filter: [filterType: filter, additionalInfo: additionalInfo == null ? '':additionalInfo]])
        } else {
            response.sendError(404, 'There is no such filter')
        }
    }

    private List<String> projectsInImport() {
        return Project.findAllByStateNotEqual(Project.ProjectState.fullyImported).collect{Project project -> project.name}
    }

    private def getFilterServiceMap() {
        [commentedChangesets: commentedChangesetsFilterService,
                myCommentsAndChangesets: myCommentsAndChangesetsFilterService,
                fileFilter: fileFilterService]
    }
}

