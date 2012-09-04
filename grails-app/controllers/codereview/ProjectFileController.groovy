package codereview

import grails.converters.JSON

//FIXME add tests
class ProjectFileController {

    def scmAccessService
    def snippetWithCommentsService
    def diffAccessService
    static def textFileFormats = [
            "java",
            "groovy",
            "html",
            "htm",
            "jsp",
            "gsp",
            "py",
            "rb",
            "h",
            "c",
            "cpp",
            "txt",
            "md",
            "php",
            "",
            "css",
            "xml",
            "javascript",
            "json"
    ]

    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    def getFileWithContent(Long id) {
        def projectFile = ProjectFile.findById(id)
        def fileContent = scmAccessService.getFileContent(projectFile)

        render([content: fileContent, filetype: projectFile.fileType, name: projectFile.name, isText: isKnownTextFormat(projectFile.fileType)] as JSON)
    }


    Boolean isKnownTextFormat(String fileType) {
        textFileFormats.contains(fileType)
    }

    def getLineCommentsWithSnippetsToFile(Long id) {
        def projectFile = ProjectFile.findById(id)
        if (projectFile == null) {
            throw new IllegalArgumentException('No file with such id was found')
        }
        def comments = getLineComments(projectFile)
        def commentGroupsWithSnippets = getCommentsGroupsWithSnippets(projectFile, comments)
        render([
                fileType: projectFile.fileType,
                commentGroupsWithSnippets: commentGroupsWithSnippets,
                commentsCount: comments.size()
        ] as JSON)
    }

    private List<Map<String, Object>> getLineComments(ProjectFile projectFile) {
        List<LineComment> comments = snippetWithCommentsService.getCommentsFromDatabase(projectFile.name, projectFile.changeset.project.name)
        def commentsProperties = comments.collect { LineComment comment ->
            def properties = comment.properties + [
                    fromRevision: getRevisionType(projectFile.changeset, comment.projectFile.changeset),
                    belongsToCurrentUser: comment.author == authenticatedUser,
                    author: comment.author.email
            ]
            properties.keySet().retainAll('id', 'author', 'dateCreated', 'lineNumber', 'projectFile', 'text', 'fromRevision', 'belongsToCurrentUser')
            properties
        }
        commentsProperties
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
    private List<Map<String, Object>> getCommentsGroupsWithSnippets(ProjectFile projectFile, List<LineComment> comments) {
        def commentGroupsWithSnippets = []
        if (!comments.isEmpty()) {
            def fileContent = scmAccessService.getFileContent(projectFile)
            def commentsGroupedByLineNumber = snippetWithCommentsService.prepareCommentGroups(comments)
            commentGroupsWithSnippets = snippetWithCommentsService.prepareCommentGroupsWithSnippets(commentsGroupedByLineNumber, projectFile.fileType, fileContent)
        }
        commentGroupsWithSnippets
    }

    def getDiff(Long id) {
        def projectFile = ProjectFile.findById(id)
        def diff = diffAccessService.getDiffWithPreviousRevisionFor(projectFile)
        render([
                diff: diff.split("\n").collect() { [line: it] },
                fileId: projectFile.id,
                rawDiff: diff,
                fileType: projectFile.fileType
        ] as JSON)
    }

}

