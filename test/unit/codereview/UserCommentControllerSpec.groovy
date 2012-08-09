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
        controller.authenticatedUser = null
        User author = User.build()
        UserComment comment = UserComment.build(author: author, text: "Very well indeed.")

        when:
        controller.returnCommentsToChangeset(comment.changeset.identifier)

        then:
        response.json.size() == 1
        def commentJSON = response.json.first()
        commentJSON.keySet() == ['text', 'author', 'dateCreated', 'belongsToCurrentUser'] as Set
        commentJSON.author == author.username
        commentJSON.text == comment.text
        commentJSON.belongsToCurrentUser == false
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
    def "should demand authorization to add comment"() {

    }

    @Ignore //FIXME implement
    def "should throw excpetion for unknown changeset"() {

    }

    def "should mark logged in user's UserComment-s as theirs"() {
        given:
        User loggedInUser = User.build(username: 'agj@touk.pl')
        controller.authenticatedUser = loggedInUser
        Changeset changeset = Changeset.build()
        def comment = UserComment.build(changeset: changeset, author: loggedInUser)
        UserComment.build(changeset: changeset)

        expect:
        changeset.userComments.contains(comment)
        changeset.save()

        when:
        controller.returnCommentsToChangeset(changeset.identifier)
        def json = response.json
        println json

        then:
        response.json*.belongsToCurrentUser == [true, false]
    }

    def "changeset without user should not belong to anonymous user"() {
        given:
        controller.authenticatedUser = null

        expect:
        controller.belongsToCurrentUser(UserComment.build()) == false
    }

}