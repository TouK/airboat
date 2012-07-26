package codereview

import grails.converters.JSON

class UserCommentController {

    def index() {
    }

    //TODO think which option is better: artificial or natural (compound!) keys for changesets
    def addComment(String username, String text, String changesetId) {
        def changeset = Changeset.findByIdentifier(changesetId)
        def comment = new UserComment(text: text, author: username)
        changeset?.addToUserComments(comment)
        comment.save()

        render "I did it! Saved."
    }

    //TODO rewrite all controller actions from closures to methods
    def returnCommentsToChangeset = {
        def changeset = Changeset.findByIdentifier(params.id) //TODO check that only one query is executed, refactor otherwise
        def comments = UserComment.findAllByChangeset(changeset)
        render comments as JSON
    }

    def getLastComments = {
        render UserComment.list(max: 10, sort: "dateCreated", order: "desc") as JSON
    }
}
