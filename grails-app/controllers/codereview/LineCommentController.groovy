package codereview

import grails.plugins.springsecurity.Secured

import static com.google.common.base.Preconditions.checkArgument
import grails.converters.JSON

class LineCommentController {

    def projectFileAccessService
    def infrastructureService

    @Secured('isAuthenticated()')
    def addComment(long fileId, int lineNumber, String text) {

        def projectFile = ProjectFile.findById(fileId)
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url)
        def fileContent = projectFileAccessService.getFileContent(projectFile, projectRootDirectory)

        //TODO write own, groovy assertion methods using a closure argument to defer (often costly) message evaluation
        checkArgument(projectFile != null, "No file with id ${fileId} was found")
        checkArgument(
                lineNumber > 0 && lineNumber <= fileContent.split('\n').size(),
                "Line number ${lineNumber} is out of range for file ${projectFile.properties}"
        )

        //TODO ask someone about making idea know the mixins being used here
        def lineComment = new LineComment(authenticatedUser, lineNumber, text)
        projectFile.addToLineComments(lineComment)
        projectFile.save()

        redirect(controller: 'projectFile', action: 'getLineCommentsWithSnippetsToFile', params: [id: projectFile.id])
    }
}
