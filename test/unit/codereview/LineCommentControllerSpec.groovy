package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON
import testFixture.Fixture


@TestFor(LineCommentController)
@Mock([Changeset, UserComment, ProjectFile, LineComment, Project])
class LineCommentControllerSpec extends Specification {

    def setup() {
        def testMsg = "should return comments to project file when given right project file i"
        controller.infrastructureService = Mock(InfrastructureService)
        controller.projectFileAccessService = Mock(ProjectFileAccessService)
        controller.projectFileAccessService.getFileContent(_, _) >>        //TODO: refactor this
                " +" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg  + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n" + testMsg+ "\n"+ testMsg+ "\n"
    }

    def "should return comments to project file when given right project file id"() {


    }

    def "should return last comments"() {
        given:
        def testProject = new Project("codereview",Fixture.PROJECT_REPOSITORY_URL)
        def changeset = new Changeset("hash23", "agj", "zmiany", new Date())
        def projectFile = new ProjectFile("info.txt", "read manuals!")
        testProject.addToChangesets(changeset)
        changeset.addToProjectFiles(projectFile)
        testProject.save()
        def fileId = projectFile.id
        def lineNumbers = [4, 2, 12]
        def texts = ["wrong indentation, boy!", "what do you mean?", "well done"]

        when:
        controller.addComment(texts[0], lineNumbers[0].toString(), fileId.toString(), "jil")
        controller.addComment(texts[1], lineNumbers[1].toString(), fileId.toString(), "jil")
        controller.addComment(texts[2], lineNumbers[2].toString(), fileId.toString(), "jil")
        controller.returnCommentsToProjectFile(fileId.toString())
        String rendered = (response.contentAsString)

        then:
        rendered != null
        texts.size() == JSON.parse(rendered).size()
        JSON.parse(rendered)[0].toString().contains('"id":1,')
        JSON.parse(rendered)[1].toString().contains('"id":2,')
        JSON.parse(rendered)[2].toString().contains('"id":3,')

    }

    def "should add comment correctly to db"() {
        given:
        def testProject = new Project("codereview",Fixture.PROJECT_REPOSITORY_URL)
        def changeset = new Changeset("hash23", "agj", "zmiany", new Date())
        def projectFile = new ProjectFile("info.txt", "read manuals!")
        testProject.addToChangesets(changeset)
        changeset.addToProjectFiles(projectFile)
        testProject.save()
        def fileId = projectFile.id
        def lineNumber = 4
        String text = "wrong indentation, boy!"

        when:
        controller.addComment(text, lineNumber.toString(), fileId.toString(), "jil")
        def lineComment =  LineComment.findByText(text)


        then:
        LineComment != null
        LineComment.list().size() == 1
        LineComment.findByProjectFile(projectFile) != null

        lineComment != null
        lineComment.text == text
        lineComment.projectFile.id == fileId

    }

}


