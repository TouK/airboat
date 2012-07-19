package codereview

import grails.converters.JSON

class UserCommentController {

    def index() { }
    def addComment = {
        def username = params.username
        def content = params.content
        def changesetId = params.changesetId
        new UserComment(content: content, author: username).save()
        //add to changeset

        render "I did it! Saved."
    }
    def receiveJSON = {
        //def json = request.JSON
        def username = params.username
        def content = params.content
        def result = username + " wrote: " + content
        render result
    }
    def getLastComments = {
        render UserComment.list(max: 10, sort: "author", order: "desc") as JSON
    }
}
