package codereview

import grails.converters.JSON

import static com.google.common.base.Strings.isNullOrEmpty
import com.google.common.annotations.VisibleForTesting

class ChangesetController {

    ScmAccessService scmAccessService
    SnippetWithCommentsService snippetWithCommentsService

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
        render changesetsProperties as JSON
    }

    @VisibleForTesting
    boolean belongsToCurrentUser(Changeset changeset) {
        authenticatedUser != null && authenticatedUser == changeset.commiter?.user
    }

    def getChangesetFiles = {
        def changeset = Changeset.findByIdentifier(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        def fileProperties = files.collect this.&getFileJSONProperties
        render fileProperties as JSON
    }

    private def getFileJSONProperties(ProjectFile file) {
        def fileProperties = file.properties +[
                id: file.id
        ]
        fileProperties
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
        render changesetsProperties as JSON
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
                allComments: getAllComments(changeset)
        ]
    }

    private String getEmail(Commiter commiter) {
        commiter.user?.email ?: commiter.email
    }

    private Integer getAllComments(Changeset changeset) {
        def allLineComments = 0
        List<LineComment> lineComments
        if (changeset.projectFiles != null) {
            changeset.projectFiles.name.each { projectFileName ->
                lineComments = snippetWithCommentsService.getCommentsFromDatabase(projectFileName, changeset.project.name)
                if (lineComments != null) {
                    allLineComments += lineComments.size()
                }
            }
        }
        else {
            allLineComments += 0
        }
        return (allLineComments + changeset.commentsCount)
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

