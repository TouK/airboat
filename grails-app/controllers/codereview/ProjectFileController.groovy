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

    def getFileWithContent(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def fileContent = scmAccessService.getFileContent(changeset, projectFile)
        render([content: fileContent, filetype: projectFile.fileType, name: projectFile.name, isText: projectFile.textFormat] as JSON)
    }

    def getLineCommentsWithSnippetsToFile(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def comments = getLineComments(changeset, projectFile)
        def commentGroupsWithSnippets = getCommentsGroupsWithSnippets(changeset, projectFile, comments)
        render([
                fileType: projectFile.fileType,
                commentGroupsWithSnippets: commentGroupsWithSnippets,
                commentsCount: comments.size()
        ] as JSON)
    }

    private List<Map<String, Object>> getLineComments(Changeset changeset, ProjectFile projectFile) {
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        checkArgument(projectFileInChangeset != null, "${projectFile} is not associated with ${changeset}")
        def threadPositions = projectFileInChangeset.commentThreadsPositions
        def commentsProperties = threadPositions.collect { ThreadPositionInFile threadPosition ->
            def comments = threadPosition.thread.comments
            comments.collect { LineComment comment ->
                def properties = comment.properties + [
                        fromRevision: getRevisionType(changeset, changeset), //FIXME count changesets between original comment posting changeset and changeset its displayed in
                        belongsToCurrentUser: comment.author == authenticatedUser,
                        author: comment.author.email,
                        lineNumber: threadPosition.lineNumber,
                        dateCreated: comment.dateCreated.format('yyyy-MM-dd HH:mm')
                ]
                properties.keySet().retainAll('id', 'author', 'dateCreated', 'lineNumber', 'projectFile', 'text', 'fromRevision', 'belongsToCurrentUser')
                properties
            }
        }
        commentsProperties.flatten()
    }

    String getRevisionType(Changeset currentCommentChangesetDate, Changeset commentChangeset) {
        if (currentCommentChangesetDate.date < commentChangeset.date) {
            'future'
        } else if (currentCommentChangesetDate.date == commentChangeset.date) {
            'current'
        } else {
            'past'
        }
    }

    //FIXME modify type
    private List<Map<String, Object>> getCommentsGroupsWithSnippets(
            Changeset changeset,
            ProjectFile projectFile,
            List<Map<String, Object>> comments
    ) {
        def commentGroupsWithSnippets = []
        if (!comments.isEmpty()) {
            def fileContent = scmAccessService.getFileContent(changeset, projectFile)
            def commentsGroupedByLineNumber = snippetWithCommentsService.prepareCommentGroups(comments)
            commentGroupsWithSnippets = snippetWithCommentsService.prepareCommentGroupsWithSnippets(commentsGroupedByLineNumber, projectFile.fileType, fileContent)
        }
        commentGroupsWithSnippets
    }

    def getDiffWithPreviousRevision(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def diff = diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)
        render([
                diff: diff.split("\n").collect() { [line: it] },
                fileId: projectFile.id,
                rawDiff: diff,
                fileType: projectFile.fileType
        ] as JSON)
    }

}

