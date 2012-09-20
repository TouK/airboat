package codereview

import grails.converters.JSON

import static com.google.common.base.Strings.isNullOrEmpty

class ChangesetController {

    ScmAccessService scmAccessService
    ReturnCommentsService returnCommentsService

    def filterFunctions = [commentedChangesets: this.&getCommentedChangesets]
    def nextFilterFunctions = [commentedChangesets: this.&getNextCommentedChangesets]

    def index() {
        def projectName = params.projectName
        def changesetId = params.changesetId
        def filter = params.filter
        if (projectName != null && changesetId != null) {
            changesetResponse(projectName, changesetId)
        } else if (projectName != null) {
            projectResponse(projectName)
        } else if (filter != null) {
            filterResponse(filter)
        } else {
            render(view: 'index', model: [projects: Project.all, type: 'project', singleProject: false])
        }
    }

    def getLastChangesets(String projectName) {
        def changesets
        if (isNullOrEmpty(projectName)) {
            changesets = Changeset.list(max: Constants.FIRST_LOAD_CHANGESET_NUMBER, sort: 'date', order: 'desc')
        } else {
            changesets = getLastChagesetsFromProject(projectName)
        }
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render changesetsProperties as JSON
    }

    def getLastFilteredChangesets(String filterType) {
        def changesets = filterFunctions.get(filterType)()
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render changesetsProperties as JSON
    }

    private List<Changeset> getLastChagesetsFromProject(String projectName) {
        //TODO examine number of queries and try to make it 1.
        def project = Project.findByName(projectName)
        Changeset.findAllByProject(project, [max: Constants.FIRST_LOAD_CHANGESET_NUMBER, sort: 'date', order: 'desc'])
    }

    def getNextFewChangesetsOlderThan(Long changesetId) {
        renderChangesetsGroups(getNextFewChangesetsFromAllProjects(changesetId))
    }

    def getNextFewChangesetsOlderThanFromSameProject(Long changesetId) {
        renderChangesetsGroups(getNextFewChangesetsFromSameProject(changesetId))
    }

    def getNextFewFilteredChangesetsOlderThan(Long changesetId, String filterType) {
        renderChangesetsGroups(nextFilterFunctions.get(filterType)(changesetId))
    }

    private void renderChangesetsGroups(List<Changeset> nextFewChangesets) {
        def changesetsProperties = nextFewChangesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render changesetsProperties as JSON
    }

    private def groupChangesetPropertiesByDay(changesetsProperties) {
        changesetsProperties.groupBy { it.date.substring(0, 10) }
    }

    private List<Changeset> getNextFewChangesetsFromAllProjects(Long changesetId) {
        Changeset.where {
            date < property(date).of { id == changesetId }
        }.list(max: Constants.NEXT_LOAD_CHANGESET_NUMBER, sort: 'date', order: 'desc')
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
            maxResults(Constants.NEXT_LOAD_CHANGESET_NUMBER)
            order('date', 'desc')
        }
    }

    private def getCommentedChangesets() {
        return Changeset.findAll("from Changeset c where userComments.size > 0 or \
                                    exists (from ProjectFileInChangeset p where p.changeset = c and \
                                    exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and pos.thread.comments.size > 0)) \
                                    order by c.date desc", [max: Constants.FIRST_LOAD_CHANGESET_NUMBER]);
    }

    private def getNextCommentedChangesets(Long changesetId) {
        def lastChangeset = Changeset.get(changesetId);
        return Changeset.findAll("from Changeset c where (userComments.size > 0 or \
                                    exists (from ProjectFileInChangeset p where p.changeset = c and \
                                    exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and pos.thread.comments.size > 0))) and c.date < :lastChangesetDate \
                                    order by c.date desc", [max: Constants.NEXT_LOAD_CHANGESET_NUMBER, lastChangesetDate: lastChangeset.date]);
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
                belongsToCurrentUser: belongsToCurrentUser(changeset),
                allComments: allCommentsCount(changeset),
                projectFiles: getChangesetFiles(changeset),
                comments: returnCommentsToChangeset(changeset.identifier)
        ]
    }

    private String getEmail(Commiter commiter) {
        commiter.user?.email ?: commiter.email
    }

    private boolean belongsToCurrentUser(Changeset changeset) {
        authenticatedUser != null && authenticatedUser == changeset.commiter.user
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
                'id', 'name', 'textFormat', 'commentsCount', 'changeType'
        )
        projectFileProperties
    }

    private def returnCommentsToChangeset(String changesetIdentifier) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangeset(changeset)
        def commentsProperties = comments.collect returnCommentsService.&getCommentJSONproperties
        commentsProperties
    }

    private def changesetResponse(projectName, changesetId) {
        def changeset = Changeset.findByIdentifierAndProject(changesetId, Project.findByName(projectName))
        if (changeset != null) {
            def changesetProperties = convertToChangesetProperties(changeset)
            render(view: 'index', model: [projects: Project.all,
                    changeset: groupChangesetPropertiesByDay([changesetProperties]) as JSON,
                    changesetId: changesetId,
                    projectName: projectName,
                    type: 'changeset'])
        } else {
            response.sendError(404, 'Changeset not found')
        }
    }

    private def projectResponse(projectName) {
        if (Project.findByName(projectName) != null) {
            render(view: 'index', model: [projects: Project.all, type: 'project', singleProject: true, projectName: projectName])
        } else {
            response.sendError(404, 'Project not found')
        }
    }

    private def filterResponse(filter) {
        if (filter in filterFunctions.keySet()) {
            render(view: 'index', model: [projects: Project.all, type: 'filter', filterType: filter])
        } else {
            response.sendError(404, 'There is no such filter')
        }
    }
}

