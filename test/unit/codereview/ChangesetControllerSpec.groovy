package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

@TestFor(ChangesetController)
@Mock(Changeset)
class ChangesetControllerSpec extends Specification {

    def setup() {
        controller.scmAccessService = Mock(ScmAccessService)
    }

    def "getLastChangesets should return JSON"() {

        given:
            new Changeset("hash23", "coding", new Date()).save()
            new Changeset("hash24", "coding", new Date()).save()
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
            new Changeset(latestChangesetId, "coding", new Date(3)).save()
            new Changeset("hash24", "coding", new Date(2)).save()
            new Changeset("hash23", "coding", new Date(1)).save()

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
        new Changeset("hash23", "coding", new Date()).save()
        new Changeset("hash24", "coding", new Date()).save()

        when:
        controller.getLastChangesets()
        String rendered = (response.contentAsString)

        then:
        rendered.contains("[")
        rendered.contains("]")
        rendered.contains("{")
        rendered.contains("}")
    }


}
