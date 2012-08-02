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
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(projectFile.changeset.project.url)   //TODO something about ProjectFile and Changeset relationship
        def path = projectRootDirectory.getAbsolutePath()
        projectFile.content = projectFileAccessService.fetchFileContentFromPath(path, projectFile.name)

        //TODO show a message for non-text files that they cannot be displayed
        //how can we do something like that? What about adding property to file called displayable? And set in in constructor,
        //and then use it in javascript accessing to it from JSON? However it would make js-scripts more complicated.
        //because, they'd have to check it before rendering and pass other arguments to render

        render([content: projectFile.content, filetype: projectFile.fileType] as JSON)
    }
    def getLineCommentsWithSnippetsToFile(Long id) {
        def projectFile = ProjectFile.findById(id)
        def comments = LineComment.findAllByProjectFile(projectFile)
        if (!comments.isEmpty()) {
        def snippetsGroup  = []
        def i = 0
        def j = 0
        def commentsWithSnippets = comments.sort { it.lineNumber }
        def lastLineNumber = comments[0].lineNumber
        comments.each{
           if(it.lineNumber == lastLineNumber) {
               if (snippetsGroup[i] == null) {
                   snippetsGroup[i] = []
               }
               snippetsGroup[i][j++] = it

           }
           else {
               lastLineNumber = it.lineNumber
               i++
               snippetsGroup[i] = []
               j = 0
               snippetsGroup[i][j++] = it
           }
        }

        def commentGroupsWithSnippets = snippetsGroup.collect {
            def snippet = getSnippet(it[0])
            [commentGroup: it, snippet: snippet, filetype: projectFile.fileType]
        }
            render commentGroupsWithSnippets as JSON
        }
        else {
            render "[ ]"
        }
    }

    def getSnippet(LineComment comment) {
        def projectRootDirectory = infrastructureService.getProjectWorkingDirectory(comment.projectFile.changeset.project.url)
        def path = projectRootDirectory.getAbsolutePath()
        def fileContent =  projectFileAccessService.fetchFileContentFromPath(path, comment.projectFile.name)
        return getLinesAround(fileContent, comment.lineNumber, 3)
    }

    def getLinesAround(String text, Integer at, Integer howMany){
        def splitted = text.split("\n")
        def from = at - (howMany -1)/2
        def to  = at + (howMany)/2
        if (from < 0) {
            from = 0
            to = from + howMany

        }
        if (to >= splitted.size()) {
            to = splitted.size() - 1
            from = to - howMany
            if (from < 0) {from = 0 }
        }
        return splitted[from.toInteger()..to.toInteger()].join("\n")
    }
}
