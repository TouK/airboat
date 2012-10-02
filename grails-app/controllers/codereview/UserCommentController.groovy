package airboat

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import static com.google.common.base.Preconditions.checkArgument

class UserCommentController {

    ReturnCommentsService returnCommentsService

    def index() {
    }

    //TODO think which option is better: artificial or natural (compound!) keys for changesets
    @Secured('isAuthenticated()')
    def addComment(String changesetIdentifier, String text) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        checkArgument(changeset != null, "Unknown changeset: ${changesetIdentifier}")
        def userComment = new UserComment(author: authenticatedUser, text: text)
        changeset.addToUserComments(userComment)
        userComment.validate()
        if (userComment.hasErrors()) {
            render(userComment.errors as JSON)
        }
        else {
            userComment.save()
            render returnCommentsService.getCommentJSONproperties(userComment) as JSON
        }
    }
}
