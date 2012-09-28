package codereview

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

    def getLineCommentsWithSnippetsToFile(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def threads = getLineCommentsInThreads(changeset, projectFile)
        def threadGroupsWithSnippets = getThreadsGroupsWithSnippets(changeset, projectFile, threads)
        render([
                fileType: projectFile.fileType,
                threadGroupsWithSnippets: threadGroupsWithSnippets,
                commentsCount: threads.collect{it.commentsCount}.sum()
        ] as JSON)
    }

    private getLineCommentsInThreads(Changeset changeset, ProjectFile projectFile) {
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        checkArgument(projectFileInChangeset != null, "${projectFile} is not associated with ${changeset}")
        def threadPositions = projectFileInChangeset.commentThreadsPositions

        return threadPositions.collect { ThreadPositionInFile threadPosition ->
            def comments = threadPosition.thread.comments
            comments = comments.collect { LineComment comment ->
                def properties = comment.properties + [
                        author: comment.author.email,
                        dateCreated: comment.dateCreated.format('yyyy-MM-dd HH:mm'),
                        date: comment.dateCreated
                ]
                properties.keySet().retainAll('id', 'author', 'dateCreated', 'text', 'date')
                properties
            }
            [threadId: threadPosition.thread.id, lineNumber: threadPosition.lineNumber, comments: comments.sort{it.date}, commentsCount: comments.size(), projectFileId: threadPosition.projectFileInChangeset.projectFile.id]
        }
    }

    private getThreadsGroupsWithSnippets(Changeset changeset, ProjectFile file, List<Map<String, Object>> threads
    ) {
        if (!threads.isEmpty()) {
            def fileContent = scmAccessService.getFileContent(changeset, file)

            return snippetWithCommentsService.prepareThreadGroupsWithSnippets(threads, fileContent)
        }
        return threads
    }

}

