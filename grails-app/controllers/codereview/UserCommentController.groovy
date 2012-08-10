package codereview

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import static com.google.common.base.Preconditions.checkArgument

class UserCommentController {

    def index() {
    }

    //TODO think which option is better: artificial or natural (compound!) keys for changesets
    @Secured("isAuthenticated()")
    def addComment(String changesetId, String text) {
        def changeset = Changeset.findByIdentifier(changesetId)
        checkArgument(changeset != null, "Unknown changeset: ${changesetId}")

        //FIXME make comment belongsTo User
        def comment = new UserComment(author: authenticatedUser, text: text)
        changeset.addToUserComments(comment)
        comment.save(failOnError: true)

        render getCommentJSONproperties(comment) as JSON
    }


    def returnCommentsToChangeset(String id) {
        def changeset = Changeset.findByIdentifier(id) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangeset(changeset)
        def commentsProperties = comments.collect this.&getCommentJSONproperties
        render commentsProperties as JSON
    }

    private def getCommentJSONproperties(UserComment userComment) {
        def commentProperties = userComment.properties + [
                belongsToCurrentUser: userComment.author == authenticatedUser,
                author: userComment.author.username
        ]
        commentProperties.keySet().retainAll('text', 'author', 'dateCreated', 'belongsToCurrentUser')
        commentProperties
    }

}
