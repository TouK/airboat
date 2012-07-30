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
        def projectFile = ProjectFile.findById(id)
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(Fixture.PROJECT_REPOSITORY_URL)
        def path = projectRootDirectory.getAbsolutePath()
        projectFile.content = projectFileAccessService.fetchFileContentFromPath(path, projectFile.name)

        //TODO show a message for non-text files that they cannot be displayed
        //how can we do something like that? What about adding property to file called displayable? And set in in constructor,
        //and then use it in javascript accessing to it from JSON? However it would make js-scripts more complicated.
        //because, they'd have to check it before rendering and pass other arguments to render

        render([content: projectFile.content] as JSON)
    }
}
