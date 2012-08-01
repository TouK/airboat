package codereview

import grails.converters.JSON

class LineCommentController {

    def index() { }

    def addComment(String text, String lineNumber, String fileId, String author) {

        def projectFile = ProjectFile.findById(fileId)

        if (projectFile != null) {
          def lineComment = new LineComment(lineNumber.toInteger(), text, author)
            projectFile.addToLineComments(lineComment)
            projectFile.save()
        }
    }
    def returnCommentsToProjectFile(String id) {
        def projectFile = ProjectFile.findById(id)

        if (projectFile != null) {
            def lineComments = LineComment.findAllByProjectFile(projectFile)
            render lineComments as JSON

        }
    }
}
