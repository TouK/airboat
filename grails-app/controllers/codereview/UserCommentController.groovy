package codereview

import grails.converters.JSON

class UserCommentController {

    def index() {
    }

    def addComment = {
        def username = params.username
        def content = params.content
        def changesetId = params.changesetId
        def changeset = Changeset.findById(params.changesetId)      //TODO:refactor this, add new method "findChangesetId"
        def comment = new UserComment(content: content, author: username)
        changeset?.addToUserComments(comment)       //TODO: refactor , add new method "saveUserCommentsInDB"
        comment.save()

        render "I did it! Saved."
    }


    def returnCommentsToChangeset = {
        def changeset = Changeset.findById(params.id)                //TODO: refactor this, add new method  "findUserComments" and use   "findChangesetId"
        def comments = UserComment.findAllByChangeset(changeset)
        render comments as JSON
    }

    def getLastComments = {
        render UserComment.list(max: 10, sort: "dateCreated", order: "desc") as JSON
    }
}
