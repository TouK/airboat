package airboat

import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService

@Build([UserComment, User])
class CommentConverterServiceSpec extends Specification {

    def commentConverterService = new CommentConverterService()
    def springSecurityService = Mock(SpringSecurityService)

    def setup() {
        commentConverterService.springSecurityService = Mock(SpringSecurityService)
    }

    def 'should return comments to changeset'() {
        given:
        def loggedInUser = User.build(username: 'logged.in@airboat.com')
        commentConverterService.springSecurityService.getCurrentUser() >> loggedInUser
        Changeset changeset = Changeset.build()
        UserComment comment = UserComment.build(changeset: changeset, author: loggedInUser, text: 'Very well indeed.')

        when:
        def result = commentConverterService.getCommentJSONproperties(comment)

        then:
        result.keySet() == ['text', 'author', 'dateCreated'] as Set
        result.author == loggedInUser.username
        result.text == comment.text
    }
}
