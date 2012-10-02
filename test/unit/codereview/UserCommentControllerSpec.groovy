package airboat

import grails.buildtestdata.mixin.Build

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification
import mixins.SpringSecurityControllerMethodsMock

@TestFor(UserCommentController)
@Mock([Commiter, Changeset, UserComment, User, Project])
@Build([UserComment, User])
class UserCommentControllerSpec extends Specification {

    def setup() {
        controller.metaClass.mixin(SpringSecurityControllerMethodsMock)
    }

    @Ignore //FIXME implement
    def 'should demand authorization to add comment'() {

    }

    @Ignore //FIXME implement
    def 'should throw excpetion for unknown changeset'() {

    }
}