package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

@TestFor(ChangesetController)
@Mock(Changeset)
class ChangesetControllerSpec extends Specification {          //TODO make tests up to date, changesetController has more methods to test now

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


}
