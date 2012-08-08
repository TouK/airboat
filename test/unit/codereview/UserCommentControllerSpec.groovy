package codereview

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

    def "should return comments to changeset when given right changeset id"() {
        given:
        UserComment comment = UserComment.build(text: "Very well indeed.")

        when:
        controller.returnCommentsToChangeset(comment.changeset.identifier)

        then:
        response.json.size() == 1
        response.json[0].text == comment.text
    }

    def "should add comment when there is a logged in user"() {
        given:
        def loggedInUser = User.build(username: "logged.in@codereview.com")
        controller.authenticatedUser = loggedInUser
        Changeset changeset = Changeset.build()
        def text = "Very well."

        when:
        controller.addComment(changeset.identifier, text)

        then:
        UserComment.findByText(text) != null
        UserComment.findByTextAndAuthor(text, loggedInUser.username) != null
        UserComment.findByChangeset(changeset) != null
    }

    @Ignore //FIXME implement
    def "should throw exception for unknown user"() {

    }

    @Ignore //FIXME implement
    def "should throw excpetion for unknown changeset"() {

    }
}