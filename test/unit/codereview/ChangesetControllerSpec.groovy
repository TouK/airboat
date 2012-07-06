package codereview

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

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

    ChangesetImportingService changesImporterMock

    def setup() {
        changesImporterMock = Mock(ChangesetImportingService)
        controller.changesetImportingService = changesImporterMock
    }

    //TODO this is a temporary solution, implement incremental imports ASAP
    void "should delete all old changesets during updating"() {
        given:
        new Changeset("hash23", "agj", new Date()).save()

        when:
        controller.updateFromRepository()

        then:
        Changeset.count() == 0
    }

    void "should import changesets during updating and not delete any of newley imported ones"() {
        given:
        1 * changesImporterMock.importFrom(_) >> { new Changeset("hash23", "agj", new Date()).save() }

        when:
        controller.updateFromRepository()

        then:
        Changeset.count() != 0
    }

}
