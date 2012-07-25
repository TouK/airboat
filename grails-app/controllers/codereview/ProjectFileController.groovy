package codereview
import grails.converters.JSON

class ProjectFileController {
    def projectFileAccessService
    def index() { }
    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }
    def getFileWithContent() {
        def PROJECT_DIR = "/tmp/projekt/"
        def projectFile = ProjectFile.findById(params.id)
        projectFile.content = projectFileAccessService.fetchFileContentFromPath(projectFile.name, PROJECT_DIR)
        render projectFile as JSON
    }
    def fileContent() {
        [id: params.id]
    }
}
