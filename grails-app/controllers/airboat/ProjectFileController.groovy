package airboat

import grails.converters.JSON

import static com.google.common.base.Preconditions.checkArgument

//FIXME add tests
class ProjectFileController {

    def scmAccessService
    def snippetWithCommentsService
    def diffAccessService


    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    def getFileListings(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        def fileContent = projectFileInChangeset.changeType == ChangeType.DELETE ? null : scmAccessService.getFileContent(changeset, projectFile)
        def diff = diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)

        render([diff: diff,
                fileContent: fileContent,
                fileType: projectFile.fileType,
                isText: projectFile.textFormat] as JSON)
    }

    def getThreadPositionAggregatesForFile(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        checkArgument(projectFileInChangeset != null, "${projectFile} is not associated with ${changeset}")

        def threadPositionsProperties = getThreadPositionsProperties(projectFileInChangeset)
        def threadPositionsWithSnippets = addSnippetsToThreadPositions(changeset, projectFile, threadPositionsProperties)
        render(threadPositionsWithSnippets as JSON)
    }

    private List<Map<String, Object>> getThreadPositionsProperties(ProjectFileInChangeset projectFileInChangeset) {
        projectFileInChangeset.commentThreadsPositions.collect() {
            getThredPositionProperties(it)
        }
    }

    private def getThredPositionProperties(ThreadPositionInFile threadPositionInFile) {
        return [
            lineNumber: threadPositionInFile.lineNumber,
            thread: getThreadProperties(threadPositionInFile.thread)
        ]
    }

    private def getThreadProperties(CommentThread commentThread) {
        return [
            id: commentThread.id,
                comments: commentThread.comments.collect { comment ->
                    [
                            author: comment.author.email,
                            dateCreated: comment.dateCreated.format('yyyy-MM-dd HH:mm'),
                            date: comment.dateCreated,
                            text: comment.text
                    ]
                }
        ]
    }

    private addSnippetsToThreadPositions(Changeset changeset, ProjectFile file, positions) {
        def fileContent = scmAccessService.getFileContent(changeset, file)
        return snippetWithCommentsService.prepareThreadPositionsWithSnippets(positions, fileContent)
    }

}

