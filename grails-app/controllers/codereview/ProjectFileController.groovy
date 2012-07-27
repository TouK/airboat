package codereview
import grails.converters.JSON
import testFixture.Fixture

//FIXME add tests
class ProjectFileController {

    def projectFileAccessService
    def infrastructureService

    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    /**
     * TODO Project name hardcoded
     * @return
     */
    def getFileWithContent(Long id) {
      //  def PROJECT_DIR = "/tmp/projekt/" //FIXME hardcoded, does not work on production env. use ${codereview.workingDirectory}/projekt/
        def projectFile = ProjectFile.findById(id)
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(Fixture.PROJECT_REPOSITORY_URL)
        def path = projectRootDirectory.getAbsolutePath()
        projectFile.content = projectFileAccessService.fetchFileContentFromPath(path, projectFile.name)

        //TODO show a message for non-text files that they cannot be displayed
        render([content: projectFile.content] as JSON)
    }
}
