package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

@TestFor(ChangesetController)
@Mock([Changeset, ProjectFile])
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def "getLastChangesets should return JSON"() {

        given:
            new Changeset("hash23", "agj", "", new Date()).save()
            new Changeset("hash24", "kpt", "", new Date()).save()
            def changesets = Changeset.list(max: 20, sort: "date", order: "desc")

        when:
            controller.getLastChangesets()

        then:
            changesets.size() == JSON.parse(response.contentAsString).size()
            response.getContentType().startsWith("application/json")
    }

    def "initial checkout should delegate to service and display index afterwards"() {

        when:
            controller.initialCheckOut()

        then:
            1 * controller.scmAccessService.checkoutProject(_)
            response.redirectedUrl == "/changeset/index"
    }

    def "should return few next changesets older than one with given revision id as JSON"() {

        given:
            def latestChangesetId = "hash25"
            new Changeset(latestChangesetId, "kpt", "", new Date(3)).save()
            new Changeset("hash24", "kpt", "", new Date(2)).save()
            new Changeset("hash23", "agj", "", new Date(1)).save()

        when:
            controller.params.id = latestChangesetId
            controller.getNextFewChangesetsOlderThan()

        then:
            def responseChangesets = JSON.parse(response.contentAsString)
            responseChangesets[0].identifier == "hash24"
            responseChangesets[1].identifier == "hash23"
    }
    def "getLastChangesets should return table of jasonized objects" () {

        given:
        new Changeset("hash23", "agj", "", new Date()).save()
        new Changeset("hash24", "kpt", "", new Date()).save()

        when:
        controller.getLastChangesets()
        String rendered = (response.contentAsString)

        then:
        rendered.contains("[")
        rendered.contains("]")
        rendered.contains("{")
        rendered.contains("}")
    }

    def "getChangeset should return one specific changeset "() {

        given:
        def  specificChangeset = "hash24"
        new Changeset("hash23", "agj", "", new Date()).save()
        new Changeset("hash24", "kpt", "", new Date()).save()
        new Changeset("hash25", "jil", "", new Date()).save()

        when:
        controller.params.id = specificChangeset
        controller.getChangeset()

        then:
        response.json.size() == 1
        def  responseSpecificChangeset = response.json.first()
        responseSpecificChangeset.identifier == "hash24"
    }

    def "getFileNamesForChangeset should return file names from changeset "() {


        given:
        def  specificChangesetHash = "hash23"
        def testChangeset = new Changeset("hash23", "agj", "", new Date())

        def projectFile1 = new ProjectFile("test.txt", "print something" )
        def projectFile2 = new ProjectFile("test2.txt", "print something2")
        def projectFile3 = new ProjectFile("test3.txt", "print something3" )
        testChangeset.addToProjectFiles(projectFile1)
        testChangeset.addToProjectFiles(projectFile2)
        testChangeset.addToProjectFiles(projectFile3)
        testChangeset.save()

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
