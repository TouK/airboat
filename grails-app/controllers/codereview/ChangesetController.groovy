package codereview

import grails.converters.JSON

import static com.google.common.base.Strings.isNullOrEmpty
import com.google.common.annotations.VisibleForTesting

class ChangesetController {

    ScmAccessService scmAccessService
    ReturnCommentsService returnCommentsService

    def index() {
        render(view: 'index', model: [projects: Project.all])
    }

    def getLastChangesets = {
        def changesets
        if (isNullOrEmpty(params.projectName)) {
            changesets = Changeset.list(max: 21, sort: 'date', order: 'desc')
        } else {
            changesets = getLastChagesetsFromProject(params.projectName)
        }
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render changesetsProperties as JSON
    }

    @VisibleForTesting
    boolean belongsToCurrentUser(Changeset changeset) {
        authenticatedUser != null && authenticatedUser == changeset.commiter?.user
    }

    private List<Map> getChangesetFiles(Changeset changeset) {
        def files = changeset.projectFilesInChangeset
        def fileProperties = files.collect this.&getFileJSONProperties
        fileProperties
    }

    //FIXME adapte front-end to new object structure
    private def getFileJSONProperties(ProjectFileInChangeset projectFileInChangeset) {
        def projectFile = projectFileInChangeset.projectFile
        def projectFileProperties = projectFile.properties + [
                id: projectFile.id,
                commentCount: projectFileInChangeset.commentThreadsPositions*.thread*.comments?.flatten()?.size(),
                changeType: projectFileInChangeset.changeType
        ]
        projectFileProperties.keySet().retainAll(
                'id', 'name', 'textFormat', 'commentsCount', 'changeType'
        )
        projectFileProperties
    }

    def getChangeset = {
        def changeset = Changeset.findByIdentifier(params.id)
        def changesetList = [changeset]
        render changesetList as JSON
    }

    def getNextFewChangesetsOlderThan(String changesetId, String projectName) {
        def nextFewChangesets
        if (isNullOrEmpty(projectName)) {
            nextFewChangesets = getNextFewChangesetsFromAllProjects(changesetId)
        } else {
            nextFewChangesets = getNextFewChangesetsFromProject(projectName, changesetId)
        }
        def changesetsProperties = nextFewChangesets.collect this.&convertToChangesetProperties
        changesetsProperties = groupChangesetPropertiesByDay(changesetsProperties)
        render changesetsProperties as JSON
    }

    private def groupChangesetPropertiesByDay(changesetsProperties) {
        changesetsProperties.groupBy { it.date.substring(0, 10) }
    }

    private def convertToChangesetProperties(Changeset changeset) {
        [
                id: changeset.id,
                identifier: changeset.identifier,
                author: changeset.commiter.cvsCommiterId,
                email: getEmail(changeset.commiter),
                date: changeset.date.format('yyyy-MM-dd HH:mm'),
                commitComment: changeset.commitComment,
                commentsCount: changeset.commentsCount,
                projectName: changeset.project.name,
                belongsToCurrentUser: belongsToCurrentUser(changeset),
                allComments: allCommentsCount(changeset),
                changesetFiles: getChangesetFiles(changeset),
                commentsToChangeset: returnCommentsToChangeset(changeset.identifier)
        ]
    }

    private String getEmail(Commiter commiter) {
        commiter.user?.email ?: commiter.email
    }

    private int allCommentsCount(Changeset changeset) {
        def allLineComments = ThreadPositionInFile.executeQuery(
                'select position.thread.comments from ThreadPositionInFile as position where position.projectFileInChangeset.changeset = :changeset',
                [changeset: changeset]
        )
        return (allLineComments.size() + changeset.commentsCount)
    }

    def returnCommentsToChangeset(String changesetIdentifier) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangeset(changeset)
        def commentsProperties = comments.collect returnCommentsService.&getCommentJSONproperties
        commentsProperties
    }

    private List<Changeset> getLastChagesetsFromProject(String projectName) {
        def projectQuery
        def project = Project.findByName(projectName) //TODO examine number of queries and try to make it 1.
        projectQuery = Changeset.findAllByProject(project, [max: 21, sort: 'date', order: 'desc'])
        projectQuery
    }

    private List<Changeset> getNextFewChangesetsFromAllProjects(String changesetId) {
        Changeset.where {
            date < property(date).of { identifier == changesetId }
        }.list(max: 10, sort: 'date', order: 'desc')
    }

    private List<Changeset> getNextFewChangesetsFromProject(String projectName, String changesetId) {
        Changeset.findAll(
                "from Changeset as changeset \
                    where changeset.project.name = :projectName \
                    and date < (select c.date from Changeset as c \
                        where c.project.name = :projectName \
                        and c.identifier = :changesetId \
                    )\
                ",
                [projectName: projectName, changesetId: changesetId],
                //FIXME confirm that this not working since 83e1704847e6ac35103d82cfd62f8a3ab463d31e is a bug in grails
                //and report it:
                [sort: 'date', order: 'desc', max: 10]
        ).sort() { -it.date.time }
    }
}

