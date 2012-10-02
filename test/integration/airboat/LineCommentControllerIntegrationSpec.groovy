package airboat

import static airboat.TestHelpers.nLinesOfSampleText
import grails.converters.JSON
import grails.plugin.spock.IntegrationSpec
import spock.lang.Ignore

class LineCommentControllerIntegrationSpec extends IntegrationSpec {

    int numberOfLinesInProjectFile = 12
    def springSecurityService

    def controller = new LineCommentController()

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
        controller.scmAccessService.getFileContent(_, _) >> nLinesOfSampleText(n: numberOfLinesInProjectFile)
    }

    def 'should add comment correctly to db'() {
        given:
        User loggedInUser = User.build()
        springSecurityService.reauthenticate(loggedInUser.username)
        Project project = Project.build()
        Changeset changeset = Changeset.build(project: project)
        ProjectFile projectFile = ProjectFile.build(project: project)
        def projectFileInChangeset = ProjectFileInChangeset.build(changeset: changeset, projectFile: projectFile)
        def lineNumber = numberOfLinesInProjectFile
        String text = "comment text"

        when:
        controller.addComment(changeset.identifier, projectFile.id, lineNumber, text)

        then:
        LineComment.count() == 1
        ThreadPositionInFile.count() == 1
        def commentPositions = ThreadPositionInFile.findAllByProjectFileInChangeset(projectFileInChangeset)
        def comments = commentPositions*.thread*.comments.flatten()
        comments*.author == [controller.authenticatedUser]
        comments*.text == [text]
        commentPositions.lineNumber == [lineNumber]
    }

    def 'should throw an exception when adding a comment to finle in revision other than newest'() {
        given:
        User loggedInUser = User.build()
        springSecurityService.reauthenticate(loggedInUser.username)

        Project project = Project.build()
        List<Changeset> changesets = (1 .. 2).collect { Changeset.build(project: project) }
        ProjectFile projectFile = ProjectFile.build(project: project)
        changesets.each { ProjectFileInChangeset.build(changeset: it, projectFile: projectFile) }

        def lineNumber = numberOfLinesInProjectFile
        def olderChangeset = changesets[0]

        when:
        controller.addComment(olderChangeset.identifier, projectFile.id, lineNumber, "comment text")

        then:
        thrown(IllegalArgumentException)
    }

}


