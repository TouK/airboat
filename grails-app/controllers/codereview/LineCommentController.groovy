package codereview

import grails.converters.JSON
import com.sun.media.sound.InvalidDataException

class LineCommentController {

    def projectFileAccessService
    def infrastructureService

    def index() { }

    def addComment(String text, String lineNumber, String fileId, String author) {

        def projectFile = ProjectFile.findById(fileId)
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url)
        def content = projectFileAccessService.getFileContent(projectFile, projectRootDirectory)

        if (projectFile == null ) {
            throw new IllegalArgumentException("No file with such id was found")
        }
        if (lineNumber.toInteger() > content.split("\n").size() || lineNumber.toInteger() < 0){
           throw new IllegalArgumentException("Line number is invalid")
        }

          def lineComment = new LineComment(lineNumber.toInteger(), text, author)
            projectFile.addToLineComments(lineComment)
            projectFile.save()
    }

    def returnCommentsToProjectFile(String id) {
        def projectFile = ProjectFile.findById(id)

        if (projectFile != null) {
            def lineComments = LineComment.findAllByProjectFile(projectFile)
            render lineComments as JSON

        }
    }
}
