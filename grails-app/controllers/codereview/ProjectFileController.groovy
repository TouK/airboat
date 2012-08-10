package codereview

import grails.converters.JSON

//FIXME add tests
class ProjectFileController {

    def projectFileAccessService
    def infrastructureService
    def snippetWithCommentsService

    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    def getFileWithContent(Long id) {
        def projectFile = ProjectFile.findById(id)
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url)
        projectFile.content = projectFileAccessService.getFileContent(projectFile, projectRootDirectory)

        render([content: projectFile.content, filetype: projectFile.fileType, name: projectFile.name] as JSON)
    }

    def getLineCommentsWithSnippetsToFile(Long id) {
        def projectFile = ProjectFile.findById(id)
        if (projectFile == null) {
            throw new IllegalArgumentException("No file with such id was found")
        }
        def comments = getLineComments(projectFile.name, projectFile.changeset.project.name)
        def commentGroupsWithSnippets = getCommentsGroupsWithSnippets(projectFile, comments)
        render([fileType: projectFile.fileType, commentGroupsWithSnippets: commentGroupsWithSnippets] as JSON)
    }

    private List<LineComment> getLineComments(String projectFile, String project) {
        LineComment.findAll(
                "from LineComment as linecomment \
                    where linecomment.projectFile.name = :projectFile \
                    and projectFile.changeset.project.name = :project",
                [projectFile: projectFile, project: project]
        )
    }

    private ArrayList<Map<String, Object>> getCommentsGroupsWithSnippets(ProjectFile projectFile, List<LineComment> comments) {
        def commentGroupsWithSnippets = []
        if (!comments.isEmpty()) {
            def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url)
            def fileContent = projectFileAccessService.getFileContent(projectFile, projectRootDirectory)
            def snippetsGroup = snippetWithCommentsService.prepareSnippetsGroupList(comments)
            commentGroupsWithSnippets = snippetWithCommentsService.prepareCommentGroupsWithSnippets(snippetsGroup, projectFile.fileType, fileContent)
        }
        commentGroupsWithSnippets
    }
}

