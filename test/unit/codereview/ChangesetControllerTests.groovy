package codereview



import org.junit.*
import grails.test.mixin.*

@TestFor(ChangesetController)
@Mock(Changeset)
class ChangesetControllerTests {

    void testIndex() {
        controller.index()
        assert "/changeset/list" == response.redirectedUrl
    }

    void testList() {
        def model = controller.list()

        assert model.changesetInstanceList.size() == 0
        assert model.changesetInstanceTotal == 0
    }

    void testListWithAddedChangesets() {
        new Changeset("hash23", "agj", new Date()).save()
        new Changeset("hash24", "kpt", new Date()).save()

        def model = controller.list()

        assert model.changesetInstanceList.size() == 2
        assert model.changesetInstanceTotal == 2
    }

}
