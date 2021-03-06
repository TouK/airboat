package airboat

import grails.plugins.springsecurity.Secured

import static com.google.common.base.Preconditions.checkArgument
import grails.converters.JSON

class LineCommentController {

    def scmAccessService
    def commentConverterService

    @Secured('isAuthenticated()')
    def checkCanAddComment(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def latestChangesetForProjectFile = ProjectFileInChangeset.findByProjectFile(
                projectFile, [sort: 'changeset.date', order: 'desc']
        ).changeset
        render([
            canAddComment: latestChangesetForProjectFile == changeset,
            projectFileLatestChangesetIdentifier: latestChangesetForProjectFile.identifier
        ] as JSON)
    }

    //TODO test what happens when you do not pass a parameter / pass undefined / null to controller method which has a
    // long (primitive) parameter
    @Secured('isAuthenticated()')
    def addComment(String changesetIdentifier, Long projectFileId, Integer lineNumber, String text) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        def fileContent = scmAccessService.getFileContent(changeset, projectFile)

        def latestChangesetForProjectFile = ProjectFileInChangeset.findByProjectFile(
                projectFile, [sort: 'changeset.date', order: 'desc']
        ).changeset
        checkArgument(latestChangesetForProjectFile == changeset, "New threads can be started only in latest revision of a file")

        //TODO write own, groovy assertion methods using a closure argument to defer (often costly) message evaluation
        checkArgument(projectFile != null, "No file with id ${projectFileId} was found")
        checkArgument(
                lineNumber > 0 && lineNumber <= fileContent.split('\n').size(),
                "Line number ${lineNumber} is out of range for file ${projectFile.properties}"
        )

        //TODO ask someone about making idea know the mixins being used here
        def lineComment = new LineComment(authenticatedUser, text)
        def thread = new CommentThread(lineComment)
        def position = new ThreadPositionInFile(projectFileInChangeset, thread, lineNumber)
        position.validate()
        if (position.hasErrors()) {
            render(position.errors as JSON)
        } else {
            thread.save()
            redirect(controller: 'projectFile', action: 'getThreadPositionAggregatesForFile',
                    params: [changesetIdentifier: changeset.identifier, projectFileId: projectFile.id])
        }
    }

    @Secured('isAuthenticated()')
    def addReply(Long threadId, String text, String changesetIdentifier, Long projectFileId) {
        def thread = CommentThread.findById(threadId)
        checkArgument(thread != null, "No thread with id [${threadId}] found")
        def comment = new LineComment(authenticatedUser, text)
        thread.addToComments(comment)
        thread.validate()
        if (thread.hasErrors()) {
            render(thread.errors as JSON)
        } else {
            thread.save(flush: true)
            render commentConverterService.getCommentJSONproperties(comment) as JSON
        }
    }

    @Secured('isAuthenticated()')
    def addToArchive(Long commentId) {
        def comment = LineComment.findById(commentId)
        comment.isArchived = true

        comment.validate()
        if (comment.hasErrors()) {
            render (comment.errors as JSON)
        } else {
            comment.save()
            render ([success: true] as JSON)
        }
    }
}
