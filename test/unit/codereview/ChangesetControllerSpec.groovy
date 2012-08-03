package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON
import testFixture.Fixture

@TestFor(ChangesetController)
@Mock([Changeset, ProjectFile, Project])
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
        }

    def "getLastChangesets should return JSON"() {

        given:
            def testProjectName = "codereview"
            def testProject = new Project(testProjectName,Fixture.PROJECT_REPOSITORY_URL)
            testProject.addToChangesets(new Changeset("hash23", "agj", "", new Date()))
            testProject.addToChangesets(new Changeset("hash24", "kpt", "", new Date()))
            testProject.save()
            def changesets = Changeset.list(max: 20, sort: "date", order: "desc")

        when:
            controller.params.projectName = testProjectName
            controller.getLastChangesets()

        then:
            changesets.size() == JSON.parse(response.contentAsString).size()
            response.getContentType().startsWith("application/json")
    }

    def "initial checkout should delegate to service and display index afterwards"() {

        when:
            controller.initialCheckOut()

        then:
            0 * controller.scmAccessService.checkoutProject(_)
            response.redirectedUrl == "/changeset/index"
    }

    def "getLastChangesets should return table of jasonized objects" () {

        given:
        def testProject = new Project("testProject","testUrl")
        testProject.addToChangesets( new Changeset("hash23", "agj", "", new Date()))
        testProject.addToChangesets( new Changeset("hash24", "kpt", "", new Date()))
        testProject.save()

        when:
        controller.getLastChangesets()
        String rendered = (response.contentAsString)

        then:
        rendered != null
        rendered.contains("[")
        rendered.contains("]")
        rendered.contains("{")
        rendered.contains("}")
    }

    def "getChangeset should return one specific changeset "() {

        given:
        def  specificChangeset = "hash24"
        def testProject = new Project("testProject","testUrl")
        testProject.addToChangesets(new Changeset("hash23", "agj", "", new Date()))
        testProject.addToChangesets(new Changeset("hash24", "kpt", "", new Date()))
        testProject.addToChangesets(new Changeset("hash25", "jil", "", new Date()))
        testProject.save()

        when:
        controller.params.id = specificChangeset
        controller.getChangeset()

        then:
        response != null
        response.json.size() == 1
        def  responseSpecificChangeset = response.json.first()
        responseSpecificChangeset.identifier == "hash24"
    }

    def "getFileNamesForChangeset should return file names from changeset "() {


        given:
        def  specificChangesetHash = "hash23"
        def testProject = new Project("testProject","testUrl")
        def testChangeset = new Changeset("hash23", "agj", "", new Date())

        def projectFile1 = new ProjectFile("test.txt", "print something" )
        def projectFile2 = new ProjectFile("test2.txt", "print something2")
        def projectFile3 = new ProjectFile("test3.txt", "print something3" )
        testProject.addToChangesets(testChangeset)
        testChangeset.addToProjectFiles(projectFile1)
        testChangeset.addToProjectFiles(projectFile2)
        testChangeset.addToProjectFiles(projectFile3)
        testProject.save()

        when:
        controller.params.id = specificChangesetHash
        controller.getFileNamesForChangeset()
        String rendered = (response.contentAsString)

        then:
        response.json.size() == 3
        rendered.contains("name")
        rendered.contains("test.txt")
        rendered.contains("test2.txt")
        rendered.contains("test3.txt")
    }
}
