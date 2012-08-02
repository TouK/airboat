package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

@TestFor(ChangesetController)
@Mock([Project, Commiter, Changeset, ProjectFile])
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def "getLastChangesets should return JSON"() {
        given:
            def changesets = [
                    new Changeset("hash23", "coding", new Date()),
                    new Changeset("hash24", "coding", new Date())
            ]
        
            def committer = new Commiter("Artur Gajowy <agj@touk.pl>")
            committer.changesets = changesets

            def project = new Project("testProject", "testUrl")
            project.changesets = changesets
        
            committer.save()
            project.save()

        when:
            controller.getLastChangesets()

        then:
        response.getContentType().startsWith("application/json")
        response.json.size() == changesets.size()
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
//            def latestChangesetId = "hash25"
//            new Changeset(latestChangesetId, "kpt", "", new Date(3)).save()
//            new Changeset("hash24", "kpt", "", new Date(2)).save()
//            new Changeset("hash23", "agj", "", new Date(1)).save()

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
//        new Changeset("hash23", "agj", "", new Date()).save()
//        new Changeset("hash24", "kpt", "", new Date()).save()

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
//        def  specificChangeset = "hash24"
//        new Changeset("hash23", "agj", "", new Date()).save()
//        new Changeset("hash24", "kpt", "", new Date()).save()
//        new Changeset("hash25", "jil", "", new Date()).save()

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
//        def changesetHash = "hash23"
//        def commiter = new Commiter("Artur Gajowy <agj@touk.pl>")
//        def changeset = new Changeset(changesetHash, "coding", new Date())
//        def projectFile = new ProjectFile("test.txt", "file contents")
//        commiter.addToChangesets(changeset)
//        changeset.addToProjectFiles(projectFile)
//        commiter.save()

        when:
        controller.params.id = changesetHash
        controller.getFileNamesForChangeset()
        String rendered = (response.contentAsString)

        then:
        response.json.size() == 1
        rendered.contains("name")
        rendered.contains("test.txt")
    }
}
