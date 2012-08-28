package codereview

import static codereview.TestHelpers.nLinesOfSampleText
import org.spockframework.missing.ControllerIntegrationSpec

class LineCommentControllerIntegrationSpec extends ControllerIntegrationSpec {

    int numberOfLinesInProjectFile = 12
    def springSecurityService

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
        controller.scmAccessService.getFileContent(_, _) >> nLinesOfSampleText(n: numberOfLinesInProjectFile)
    }

    def 'should add comment correctly to db'() {
        given:
        User loggedInUser = User.build(username: 'logged.in@codereview.com')
        springSecurityService.reauthenticate(loggedInUser.username)
        Project project = Project.build()
        Changeset changeset = Changeset.build(project: project)
        ProjectFile projectFile = ProjectFile.build(changesets: [changeset], project: project)
        def lineNumber = numberOfLinesInProjectFile
        String text = "comment text"

        when:
        controller.addComment(changeset.identifier, projectFile.id, lineNumber, text)

        then:
        LineComment.count() == 1
        LineCommentPosition.count() == 1
        def lineComment = LineCommentPosition.findAllByChangesetAndProjectFile(changeset, projectFile)
        lineComment*.comment*.author == [controller.authenticatedUser]
        lineComment*.comment*.text == [text]
        lineComment.lineNumber == [lineNumber]
    }

}


