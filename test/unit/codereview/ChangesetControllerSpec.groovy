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
        controller.returnCommentsService = Mock(ReturnCommentsService)
    }

    Changeset changesetWithoutUser() {
        Changeset changeset = Changeset.build()
        assert changeset.commiter.user == null
        changeset
    }
}
