package codereview

import org.spockframework.missing.ControllerIntegrationSpec
import grails.test.mixin.TestFor

class UserCommentControllerIntegrationSpec extends ControllerIntegrationSpec {

    def springSecurityService

    def "should add comment when there is a logged in user"() {
        given:
        def loggedInUser = User.build(username: "logged.in@codereview.com")
//        controller.authenticatedUser = loggedInUser
        springSecurityService.reauthenticate(loggedInUser.username)

        Changeset changeset = Changeset.build()
        def text = "Very well."

        when:
        controller.addComment(changeset.identifier, text)

        then:
        UserComment.findByText(text) != null
        UserComment.findByTextAndAuthor(text, loggedInUser.username) != null
        UserComment.findByChangeset(changeset) != null
    }
}
