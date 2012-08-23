package codereview

import grails.buildtestdata.mixin.Build

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

import spock.lang.Specification
import mixins.SpringSecurityControllerMethodsMock

import static codereview.TestHelpers.nLinesOfSampleText

@TestFor(LineCommentController)
@Mock([Changeset, UserComment, ProjectFile, LineComment, Project])
@Build(User)
class LineCommentControllerSpec extends Specification {

    def setup() {
        controller.metaClass.mixin(SpringSecurityControllerMethodsMock)
        controller.infrastructureService = Mock(InfrastructureService)
        controller.scmAccessService = Mock(ScmAccessService)
        controller.scmAccessService.getFileContent(_) >> nLinesOfSampleText(n: 12)
    }

    def 'should add comment correctly to db'() {
        given:
        controller.authenticatedUser = User.build(username: 'logged.in@codereview.com')

        def testProject = new Project('testProject', 'testUrl')
        def changeset = new Changeset('hash23', 'zmiany', new Date())
        def projectFile = new ProjectFile(name: 'info.txt', changeType: ChangeType.ADD)
        testProject.addToChangesets(changeset)
        changeset.addToProjectFiles(projectFile)
        testProject.save()
        def fileId = projectFile.id
        def lineNumber = 4
        String text = 'wrong indentation, boy!'

        when:
        controller.addComment(fileId, lineNumber, text)

        then:
        LineComment.list().size() == 1
        LineComment.findByProjectFile(projectFile) != null

        def lineComment = LineComment.findByText(text)
        lineComment != null
        lineComment.text == text
        lineComment.projectFile.id == fileId
    }

}


