package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

import spock.lang.Ignore
import grails.plugins.springsecurity.SpringSecurityService
import grails.buildtestdata.mixin.Build


@TestFor(LineCommentController)
@Mock([Changeset, UserComment, ProjectFile, LineComment, Project])
@Build(User)
class LineCommentControllerSpec extends Specification {

    SpringSecurityService springSecurityService
    User loggedInUser

    def setup() {
        springSecurityService = Mock()
        controller.metaClass.getAuthenticatedUser = {
            loggedInUser
        }
        controller.infrastructureService = Mock(InfrastructureService)
        controller.projectFileAccessService = Mock(ProjectFileAccessService)
        controller.projectFileAccessService.getFileContent(_, _) >> nLinesOfSampleText(n: 12)
    }

    private String nLinesOfSampleText(Map parameters) {
        (1 .. parameters.n).collect { "line ${it}" }.join('\n')
    }

    @Ignore //FIXME implement
    def "should return comments to project file when given right project file id"() {

    }

    def "should add comment correctly to db"() {
        given:
        loggedInUser = User.build(username: "logged.in@codereview.com", springSecurityService: springSecurityService)

        def testProject = new Project("testProject","testUrl")
        def changeset = new Changeset("hash23", "zmiany", new Date())
        def projectFile = new ProjectFile("info.txt", "read manuals!")
        testProject.addToChangesets(changeset)
        changeset.addToProjectFiles(projectFile)
        testProject.save()
        def fileId = projectFile.id
        def lineNumber = 4
        String text = "wrong indentation, boy!"

        when:
        controller.addComment(fileId, lineNumber, text)

        then:
        LineComment.list().size() == 1
        LineComment.findByProjectFile(projectFile) != null

        def lineComment =  LineComment.findByText(text)
        lineComment != null
        lineComment.text == text
        lineComment.projectFile.id == fileId
    }

}


