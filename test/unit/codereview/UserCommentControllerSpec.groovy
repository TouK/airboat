package codereview


import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Ignore
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService

@TestFor(UserCommentController)
@Mock([Commiter, Changeset, UserComment, User, Project])
@Build([UserComment, User])
class UserCommentControllerSpec extends Specification {

    def "should return comments to changeset when given right changeset id"() {
        given:
        UserComment comment = UserComment.build(text: "Very well indeed.")

        when:
        controller.returnCommentsToChangeset(comment.changeset.identifier)

        then:
        response.json.size() == 1
        response.json[0].text == comment.text
    }

    def "should add comment"() {
        given:
        SpringSecurityService springSecurityService = Mock()
        User user = User.build(username: "agj@touk.pl", springSecurityService: springSecurityService)
        Changeset changeset = Changeset.build()
        def text = "Very well."

        when:
        controller.addComment(user.username, changeset.identifier, text)

        then:
        UserComment.findByText(text) != null
        UserComment.findByTextAndAuthor(text, user.username) != null
        UserComment.findByChangeset(changeset) != null
    }

    @Ignore //FIXME implement
    def "should throw exception for unknown user"() {

    }

    @Ignore //FIXME implement
    def "should throw excpetion for unknown changeset"() {

    }
}