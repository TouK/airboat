package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

@TestFor(ChangesetController)
@Mock([Commiter, Changeset, ProjectFile])
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def "getLastChangesets should return JSON"() {

        given:
            new Commiter("Artur Gajowy <agj@touk.pl>")
                .addToChangesets(new Changeset("hash23", "coding", new Date()))
                .addToChangesets(new Changeset("hash24", "coding", new Date()))
                .save()
            def changesets = Changeset.list(max: 20, sort: "date", order: "desc")

        when:
            controller.getLastChangesets()

        then:
            changesets.size() == response.json.size()
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
            new Commiter("Artur Gajowy <agj@touk.pl>")
                .addToChangesets(new Changeset(latestChangesetId, "coding", new Date(3)))
                .addToChangesets(new Changeset("hash24", "coding", new Date(2)))
                .addToChangesets(new Changeset("hash23", "coding", new Date(1)))
                .save()

        when:
            controller.params.id = latestChangesetId
            controller.getNextFewChangesetsOlderThan()

        then:
            def responseChangesets = JSON.parse(response.contentAsString)
            responseChangesets[0].identifier == "hash24"
            responseChangesets[1].identifier == "hash23"
    }

    def "getChangeset should return one specific changeset "() {

        given:
        def  specificChangeset = "hash24"
        new Commiter("Artur Gajowy <agj@touk.pl>")
                .addToChangesets(new Changeset("hash25", "coding", new Date(3)))
                .addToChangesets(new Changeset(specificChangeset, "coding", new Date(2)))
                .addToChangesets(new Changeset("hash23", "coding", new Date(1)))
                .save()

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
        def changesetHash = "hash23"
        def commiter = new Commiter("Artur Gajowy <agj@touk.pl>")
        def changeset = new Changeset(changesetHash, "coding", new Date())
        def projectFile = new ProjectFile("test.txt", "file contents")
        commiter.addToChangesets(changeset)
        changeset.addToProjectFiles(projectFile)
        commiter.save()

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
