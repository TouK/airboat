package airboat

import grails.converters.JSON
import grails.plugin.spock.IntegrationSpec


class UserCommentControllerIntegrationSpec extends IntegrationSpec {

    def springSecurityService

    UserCommentController controller = new UserCommentController()

    def 'should add comment when there is a logged in user'() {
        given:
        def loggedInUser = User.build(username: 'logged.in@airboat.com')
        springSecurityService.reauthenticate(loggedInUser.username)

        Changeset changeset = Changeset.build()
        def text = 'Very well.'

        when:
        controller.addComment(changeset.identifier, text)

        then:
        UserComment.findByText(text) != null
        UserComment.findByTextAndAuthor(text, loggedInUser) != null
        UserComment.findByChangeset(changeset) != null
    }
}
