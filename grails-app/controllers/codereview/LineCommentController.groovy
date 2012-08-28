package codereview

import grails.plugins.springsecurity.Secured

import static com.google.common.base.Preconditions.checkArgument
import grails.converters.JSON

class LineCommentController {

    def scmAccessService

    //TODO test what happens when you do not pass a parameter / pass undefined / null to controller method which has a
    // long (primitive) parameter
    @Secured('isAuthenticated()')
    def addComment(String changesetIdentifier, Long projectFileId, Integer lineNumber, String text) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def fileContent = scmAccessService.getFileContent(changeset, projectFile)

        //TODO write own, groovy assertion methods using a closure argument to defer (often costly) message evaluation
        checkArgument(projectFile != null, "No file with id ${projectFileId} was found")
        checkArgument(
                lineNumber > 0 && lineNumber <= fileContent.split('\n').size(),
                "Line number ${lineNumber} is out of range for file ${projectFile.properties}"
        )

        //TODO ask someone about making idea know the mixins being used here
        def lineComment = new LineComment(authenticatedUser, text)
        def position = new LineCommentPosition(projectFile, lineComment, lineNumber)
        changeset.addToLineCommentsPositions(position)
        lineComment.validate()
        if (lineComment.hasErrors()) {
            render(lineComment.errors as JSON)
        } else {
            lineComment.save(failOnError: true)
            redirect(controller: 'projectFile', action: 'getLineCommentsWithSnippetsToFile',
                    params: [changesetIdentifier: changeset.identifier, projectFileId: projectFile.id])
        }
    }
}
