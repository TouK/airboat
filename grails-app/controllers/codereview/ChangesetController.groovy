package codereview

import grails.converters.JSON

import static com.google.common.base.Strings.isNullOrEmpty
import com.google.common.annotations.VisibleForTesting

class ChangesetController {

    ScmAccessService scmAccessService

    def index() {
        render(view: 'index', model: [projects: Project.all])
    }

    def getLastChangesets (String projectName) {
        def changesets
        if (isNullOrEmpty(projectName)) {
            changesets = Changeset.list(max: 21, sort: 'date', order: 'desc')
        } else {
            changesets = getLastChangesetsFromProject(projectName)
        }
        def changesetsProperties = changesets.collect this.&convertToChangesetProperties
        render changesetsProperties as JSON
    }

    @VisibleForTesting
    boolean belongsToCurrentUser(Changeset changeset) {
        authenticatedUser != null && authenticatedUser == changeset.commiter?.user
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

        def userComments = UserComment.findAllByChangeset(changeset).sort {it.dateCreated}
        [
                id: changeset.id,
                identifier: changeset.identifier,
                author: changeset.commiter.cvsCommiterId,
                email: getEmail(changeset.commiter),
                date: changeset.date.format('yyyy-MM-dd HH:mm'),
                commitComment: changeset.commitComment,
                commentsCount: changeset.commentsCount,
                projectName: changeset.project.name,
                userComments: userComments.collect {getCommentJSONproperties(it)},
                belongsToCurrentUser: belongsToCurrentUser(changeset),
                files: ProjectFile.findAllByChangeset(changeset)
        ]
    }

    def generateRandomPastelColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        red = (red + 255) / 2;
        green = (green + 255) / 2;
        blue = (blue + 255) / 2;
        return [red: red, green: green, blue: blue];
    }

    private String getEmail(Commiter commiter) {
        commiter.user?.email ?: commiter.email
    }

    private List<Changeset> getLastChangesetsFromProject(String projectName) {
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


    private def getCommentJSONproperties(UserComment userComment) {
        def commentProperties = userComment.properties + [
                belongsToCurrentUser: (userComment.author == authenticatedUser),
                author: userComment.author.username,
                dateCreated: userComment.dateCreated.format('yyyy-MM-dd HH:mm')
        ]
        commentProperties.keySet().retainAll('text', 'author', 'dateCreated', 'belongsToCurrentUser')
        commentProperties
    }








}

