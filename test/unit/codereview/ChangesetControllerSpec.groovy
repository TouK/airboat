package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import mixins.SpringSecurityControllerMethodsMock
import spock.lang.Ignore

@TestFor(ChangesetController)
@Build([ProjectFileInChangeset, User])
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.metaClass.mixin(SpringSecurityControllerMethodsMock)
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def 'getChangeset should return one specific changeset '() {
        given:
        def specificChangesetId = 'hash24'
        Changeset.build(identifier: specificChangesetId)
        Changeset.build()
        Changeset.build()

        when:
        controller.params.id = specificChangesetId
        controller.getChangeset()

        then:
        response.json.size() == 1
        def responseSpecificChangeset = response.json.first()
        responseSpecificChangeset.identifier == 'hash24'
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
