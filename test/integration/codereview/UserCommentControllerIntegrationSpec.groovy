package codereview

import org.spockframework.missing.ControllerIntegrationSpec
import grails.converters.JSON


class UserCommentControllerIntegrationSpec extends ControllerIntegrationSpec {

    def setup() {
         JSON.registerObjectMarshaller(Map, { Map it -> it })
    }

    def springSecurityService

    def "should add comment when there is a logged in user"() {
        given:
        def loggedInUser = User.build(username: "logged.in@codereview.com")
        springSecurityService.reauthenticate(loggedInUser.username)

        Changeset changeset = Changeset.build()
        def text = "Very well."

        when:
        controller.addComment(changeset.identifier, text)

        then:
        UserComment.findByText(text) != null
        UserComment.findByTextAndAuthor(text, loggedInUser) != null
        UserComment.findByChangeset(changeset) != null
    }
}
