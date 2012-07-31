package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON


@TestFor(LineCommentController)
@Mock([Changeset, UserComment, ProjectFile, LineComment])
class LineCommentControllerSpec extends Specification {

    def "should return comments to project file when given right project file id"() {


    }

    def "should return last comments"() {
        given:
        def changeset = new Changeset("hash23", "agj", "zmiany", new Date())
        def projectFile = new ProjectFile("info.txt", "read manuals!")
        changeset.addToProjectFiles(projectFile)
        changeset.save()
        def fileId = projectFile.id
        def lineNumbers = [4, 2, 12]
        def texts = ["wrong indentation, boy!", "what do you mean?", "well done"]

        when:
        controller.addComment(texts[0], lineNumbers[0], fileId)
        controller.addComment(texts[1], lineNumbers[1], fileId)
        controller.addComment(texts[2], lineNumbers[2], fileId)
        controller.returnCommentsToProjectFile(fileId.toString())
        String rendered = (response.contentAsString)

        then:
        texts.size() == JSON.parse(rendered).size()
        JSON.parse(rendered)[0].toString().contains('"id":1,"text":"wrong')
        JSON.parse(rendered)[1].toString().contains('"id":2,"text":"what')
        JSON.parse(rendered)[2].toString().contains('"id":3,"text":"well')

    }

    def "should add comment correctly to db"() {
        given:
        def changeset = new Changeset("hash23", "agj", "zmiany", new Date())
        def projectFile = new ProjectFile("info.txt", "read manuals!")
        changeset.addToProjectFiles(projectFile)
        changeset.save()
        def fileId = projectFile.id
        def lineNumber = 4
        String text = "wrong indentation, boy!"

        when:
        controller.addComment(text, lineNumber, fileId)
        def lineComment
        lineComment =  LineComment.findByText(text)
        then:
        LineComment.list().size() == 1
        LineComment.findByProjectFile(projectFile) != null

        lineComment != null
        lineComment.text == text
        lineComment.projectFile.id == fileId

    }

}


