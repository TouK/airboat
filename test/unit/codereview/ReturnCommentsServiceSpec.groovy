package codereview

import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService

@Build([UserComment, User])
class ReturnCommentsServiceSpec extends Specification {

    def returnCommentsService = new ReturnCommentsService()
    def springSecurityService = Mock(SpringSecurityService)

    def setup() {
        returnCommentsService.springSecurityService = Mock(SpringSecurityService)
    }

    def 'should return comments to changeset'() {
        given:
        def loggedInUser = User.build(username: 'logged.in@codereview.com')
        returnCommentsService.springSecurityService.getCurrentUser() >> loggedInUser
        Changeset changeset = Changeset.build()
        UserComment comment = UserComment.build(changeset: changeset, author: loggedInUser, text: 'Very well indeed.')

        when:
        def result = returnCommentsService.getCommentJSONproperties(comment)

        then:
        result.keySet() == ['text', 'author', 'dateCreated'] as Set
        result.author == loggedInUser.username
        result.text == comment.text
    }
}
