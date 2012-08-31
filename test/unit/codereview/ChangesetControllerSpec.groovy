package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import mixins.SpringSecurityControllerMethodsMock
import spock.lang.Ignore

@TestFor(ChangesetController)
@Build([ProjectFile, User, Changeset])
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.metaClass.mixin(SpringSecurityControllerMethodsMock)
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def 'getLastChangesets should return JSON'() {
        given:
        Project project = Project.build()
        def changesets = (1..3).collect { Changeset.build(project: project) }

        when:

        controller.getLastChangesets(project.name)

        then:
        response.getContentType().startsWith('application/json')
        response.json.size() == changesets.size()
    }




    def "should mark logged in user's changesets as theirs"() {
        given:
        controller.authenticatedUser = User.build(username: 'agj@touk.pl')
        def loggedInUsersCommitter = Commiter.build(user: controller.authenticatedUser)
        Changeset.build(commiter: loggedInUsersCommitter)
        Changeset.build(commiter: Commiter.build(user: User.build(username: 'kpt@touk.pl')))

        when:
        controller.getLastChangesets(null)

        then:
        response.json*.belongsToCurrentUser == [false, true]
    }

    def 'changeset without user should not belong to anonymous user'() {
        given:
        controller.authenticatedUser = null

        expect:
        controller.belongsToCurrentUser(changesetWithoutUser()) == false
    }

    Changeset changesetWithoutUser() {
        Changeset changeset = Changeset.build()
        assert changeset.commiter.user == null
        changeset
    }
}
