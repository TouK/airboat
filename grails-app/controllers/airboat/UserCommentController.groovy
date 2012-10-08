package airboat

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import static com.google.common.base.Preconditions.checkArgument

class UserCommentController {

    CommentConverterService commentConverterService

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
            userComment.save(flush: true)
            render commentConverterService.getCommentJSONproperties(userComment) as JSON
        }
    }

    @Secured('isAuthenticated()')
    def addToArchive(Long commentId) {
        def comment = UserComment.findById(commentId)
        comment.isArchived = true

        comment.validate()
        if (comment.hasErrors()) {
            render (comment.errors as JSON)
        } else {
            comment.save()
            render ([success: true] as JSON)
        }
    }
}
