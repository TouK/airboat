package codereview

import grails.converters.JSON

class UserCommentController {

    def index() {
    }

    def addComment = {
        def username = params.username
        def content = params.content
        def changesetId = params.changesetId
        def changeset = Changeset.findById(params.changesetId)
        def comment = new UserComment(content: content, author: username)
        changeset?.addToUserComments(comment)
        comment.save()

        render "I did it! Saved."
    }

    def receiveJSON = {
        //def json = request.JSON
        def username = params.username
        def content = params.content
        def result = username + " wrote: " + content
        render result
    }
    def returnCommentsToChangeset = {
        def changeset = Changeset.findById(params.id)
        def comments = UserComment.findAllByChangeset(changeset)
        render comments as JSON
    }
    def getLastComments = {
        render UserComment.list(max: 10, sort: "dateCreated", order: "desc") as JSON
    }
}
