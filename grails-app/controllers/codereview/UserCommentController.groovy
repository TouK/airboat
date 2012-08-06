package codereview

import grails.converters.JSON

class UserCommentController {

    def index() {
    }

    //TODO think which option is better: artificial or natural (compound!) keys for changesets
    def addComment(String username, String changesetId, String text) {
        def changeset = Changeset.findByIdentifier(changesetId)
        def comment = new UserComment(text: text, author: username)
        changeset?.addToUserComments(comment)
        comment.save()

        render "I did it! Saved."
    }


    def returnCommentsToChangeset(String id) {
        def changeset = Changeset.findByIdentifier(id) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangeset(changeset)
        render comments as JSON
    }
}
