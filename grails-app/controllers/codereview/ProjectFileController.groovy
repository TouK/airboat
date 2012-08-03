package codereview
import grails.converters.JSON
import testFixture.Fixture

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

        render([content: projectFile.content, filetype: projectFile.fileType] as JSON)
    }

    def getLineCommentsWithSnippetsToFile(Long id) {
        def projectFile = ProjectFile.findById(id)
        if (projectFile == null) {
            throw new IllegalArgumentException("No file with such id was found")
        }

        def comments = LineComment.findAllByProjectFile(projectFile)
        if (!comments.isEmpty()) {
            def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url)
            def fileContent = projectFileAccessService.getFileContent(projectFile, projectRootDirectory)
            def snippetsGroup  = snippetWithCommentsService.prepareSnippetsGroupList(comments)
            def commentGroupsWithSnippets = snippetWithCommentsService.prepareCommentGroupsWithSnippets(snippetsGroup, projectFile.fileType, fileContent)
            render commentGroupsWithSnippets as JSON
        }
        else {
            render "[ ]"
        }
    }

}
