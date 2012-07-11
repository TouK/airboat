package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

@TestFor(ChangesetController)
@Mock(Changeset)
class ChangesetControllerSpec extends Specification {

    void testIndex() {
        when:
        controller.index()

        then: "/changeset/list" == response.redirectedUrl
    }

    void testList() {
        when:
        def model = controller.list()

        then:
        model.changesetInstanceList.size() == 0
        model.changesetInstanceTotal == 0
    }

    void testListWithAddedChangesets() {
        when:
        new Changeset("hash23", "agj", new Date()).save()
        new Changeset("hash24", "kpt", new Date()).save()

        def model = controller.list()

        then:
        model.changesetInstanceList.size() == 2
        model.changesetInstanceTotal == 2
    }

    def "should return JSON"() {
        when:
        new Changeset("hash23", "agj", new Date()).save()
        new Changeset("hash24", "kpt", new Date()).save()
            def jasonList = Changeset.list(max: 20, sort: "date", order: "desc") as JSON


        then:
        jasonList != null
    }
    ChangelogAccessService changelogAccessServiceMock

    def setup() {
        changelogAccessServiceMock = Mock(ChangelogAccessService)
        controller.changelogAccessService = changelogAccessServiceMock
    }

}
