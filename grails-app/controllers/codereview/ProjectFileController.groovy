package codereview

import grails.converters.JSON

//FIXME add tests
class ProjectFileController {

    def scmAccessService
    def infrastructureService
    def snippetWithCommentsService
    def diffAccessService

    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    def getFileWithContent(Long id) {
        def projectFile = ProjectFile.findById(id)
        def fileContent = scmAccessService.getFileContent(projectFile)
        render([content: fileContent, filetype: projectFile.fileType, name: projectFile.name] as JSON)
    }

    def getLineCommentsWithSnippetsToFile(Long id) {
        def projectFile = ProjectFile.findById(id)
        if (projectFile == null) {
            throw new IllegalArgumentException('No file with such id was found')
        }
        def comments = getLineComments(projectFile)
        def commentGroupsWithSnippets = getCommentsGroupsWithSnippets(projectFile, comments)
        render([fileType: projectFile.fileType, commentGroupsWithSnippets: commentGroupsWithSnippets] as JSON)
    }

    private List<Map<String, Object>> getLineComments(ProjectFile projectFile) {
        String fileName = projectFile.name
        String projectName = projectFile.changeset.project.name

        def comments = LineComment.findAll(
                "from LineComment as linecomment \
                    where linecomment.projectFile.name = :fileName \
                    and projectFile.changeset.project.name = :projectName \
                    order by projectFile.changeset.date asc, linecomment.dateCreated asc \
                     ",
                [fileName: fileName, projectName: projectName],
        )
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
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url).absolutePath

        File dir = new File(projectRootDirectory, "/.git")
        if (dir.exists()) {
            def diff = diffAccessService.getDiffToProjectFile(projectFile, projectRootDirectory)
            render([diff: diff.split("\n").collect() { [line: it]}, fileId: projectFile.id, rawDiff: diff, fileType: projectFile.fileType] as JSON)
        } else {
            render("No diff available, wrong working directory!")
        }
    }

}

