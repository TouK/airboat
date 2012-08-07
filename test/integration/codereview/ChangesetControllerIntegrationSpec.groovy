package codereview

import grails.converters.JSON
import grails.plugin.spock.ControllerSpec
import testFixture.Fixture
import spock.lang.Ignore
import org.spockframework.missing.ControllerIntegrationSpec

import static codereview.ScmAccessServiceIntegrationSpec.verifyDbIsClean
import static codereview.ScmAccessServiceIntegrationSpec.purgeDb
import static codereview.ScmAccessServiceIntegrationSpec.verifyDbIsClean

class ChangesetControllerIntegrationSpec extends ControllerIntegrationSpec {

    def setup() {
        //TODO this is dubious. Why would we need to change the marshaller for tests!?
        //TODO see: http://grails.1312388.n4.nabble.com/Testing-quot-render-x-as-XML-quot-with-mock-objects-td4632267.html
        JSON.registerObjectMarshaller(Changeset, { Changeset changeset -> [id: changeset.id, identifier: changeset.identifier] })
    }

    def "should return few next changesets older than one with given revision id as JSON"() {
        given:
        String latestChangesetId = '3'
        Changeset.build(identifier: latestChangesetId, date: new Date(3))
        Changeset.build(identifier: '2', date: new Date(2))
        Changeset.build(identifier: '1', date: new Date(1))

        when:
        controller.getNextFewChangesetsOlderThan(latestChangesetId, null)

        then:
        def responseChangesets = controller.response.json
        responseChangesets.size() == 2
        responseChangesets[0].identifier == 2 as String
        responseChangesets[1].identifier == 1 as String
    }

    def "getNextFewChangesetsOlderThan() should return few next changesets older one with given revision id as JSON"() {
        given:
        String latestChangesetId = '3'
        Project project = Project.build(name: 'foo')
        Changeset.build(project: project, identifier: latestChangesetId, date: new Date(3))
        Changeset.build(project: project, identifier: '2', date: new Date(2))
        Changeset.build(project: Project.build(name: 'bar'), identifier: '1', date: new Date(1))
        Changeset.build(project: project, identifier: '0', date: new Date(0))

        when:
        controller.getNextFewChangesetsOlderThan(latestChangesetId, project.name)

        then:
        def responseChangesets = controller.response.json
        responseChangesets.size() == 2
        responseChangesets[0].identifier == '2'
        responseChangesets[1].identifier == '0'
    }
}
