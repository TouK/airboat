package codereview
import grails.converters.JSON

//FIXME add tests
class ProjectFileController {

    def projectFileAccessService

    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    def getFileWithContent() {
        def PROJECT_DIR = "/tmp/projekt/" //FIXME hardcoded, does not work on production env. use ${codereview.workingDirectory}/projekt/
        def projectFile = ProjectFile.findById(params.id)
        projectFile.content = projectFileAccessService.fetchFileContentFromPath(projectFile.name, PROJECT_DIR)

        //TODO show a message for non-text files that they cannot be displayed
        render([content: projectFile.content] as JSON)
    }
}
