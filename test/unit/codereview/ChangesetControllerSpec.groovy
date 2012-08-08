package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ChangesetController)
@Build([ProjectFile, User, Changeset])
class ChangesetControllerSpec extends Specification {

    User loggedInUser

    def setup() {
        controller.metaClass.getAuthenticatedUser = {
            loggedInUser
        }
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def "getLastChangesets should return JSON"() {
        given:
        def changesets = (1..3).collect { Changeset.build() }

        when:
        controller.getLastChangesets()

        then:
        response.getContentType().startsWith("application/json")
        response.json.size() == changesets.size()
    }

    def "initial checkout should delegate to service and display index afterwards"() {
        def numberOfProjects = 3
        given:
        (1..numberOfProjects).each { Project.build() }

        when:
        controller.initialCheckOut()

        then:
        numberOfProjects * controller.scmAccessService.checkoutProject(_)
        response.redirectedUrl == "/changeset/index"
    }

    def "getChangeset should return one specific changeset "() {
        given:
        def specificChangesetId = "hash24"
        Changeset.build(identifier: specificChangesetId)
        Changeset.build()
        Changeset.build()

        when:
        controller.params.id = specificChangesetId
        controller.getChangeset()

        then:
        response.json.size() == 1
        def responseSpecificChangeset = response.json.first()
        responseSpecificChangeset.identifier == "hash24"
    }

    def "getFileNamesForChangeset should return file names from changeset "() {
        given:
        Changeset changeset = Changeset.build()
        ProjectFile projectFile = ProjectFile.build(changeset: changeset, name: "kickass!")

        when:
        controller.params.id = changeset.identifier
        controller.getFileNamesForChangeset()

        then:
        response.json.size() == 1
        response.json.first().name == projectFile.name
    }

    def "should mark logged in user's changesets as theirs"() {
        given:
        loggedInUser = User.build(username: 'agj@touk.pl')
        def loggedInUsersCommitter = Commiter.build(user: loggedInUser)
        Changeset.build(commiter: loggedInUsersCommitter)
        Changeset.build(commiter: Commiter.build(user: User.build(username: 'kpt@touk.pl')))

        when:
        controller.getLastChangesets()

        then:
        response.json*.belongsToCurrentUser == [false, true]
    }

    def "changeset without user should not belong to anonymous user"() {
        given:
        loggedInUser = null

        expect:
        controller.belongsToCurrentUser(changesetWithoutUser()) == false
    }

    Changeset changesetWithoutUser() {
        Changeset changeset = Changeset.build()
        assert changeset.commiter.user == null
        changeset
    }
}
