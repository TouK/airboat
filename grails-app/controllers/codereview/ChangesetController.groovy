package codereview

import grails.converters.JSON

import static com.google.common.base.Strings.isNullOrEmpty
import com.google.common.annotations.VisibleForTesting

class ChangesetController {

    ScmAccessService scmAccessService

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

    private String getUserEmail(Changeset changeset) {
        def user = changeset.commiter.user
        if (user == null) {
            //TODO check if the assumption that committers not always have email holds. If not, use commiter.email
            'no.such.email@codereview.touk.pl'
        } else {
            user.email
        }
    }

    @VisibleForTesting
    boolean belongsToCurrentUser(Changeset changeset) {
        authenticatedUser != null && authenticatedUser == changeset.commiter?.user
    }

    def getFileNamesForChangeset = {
        def changeset = Changeset.findByIdentifier(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        render files as JSON
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
                email: getUserEmail(changeset),
                date: changeset.date.format('yyyy-MM-dd HH:mm'),
                commitComment: changeset.commitComment,
                commentsCount: changeset.commentsCount,
                projectName: changeset.project.name,
                belongsToCurrentUser: belongsToCurrentUser(changeset)
        ]
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
                    and date < (select c.date from Changeset as c where c.identifier = :changesetId) \
                ",
                [projectName: projectName, changesetId: changesetId],
                [sort: 'date', order: 'desc', max: 10]
        )
    }
}

