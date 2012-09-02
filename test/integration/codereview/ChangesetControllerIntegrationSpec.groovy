package codereview

import grails.converters.JSON
import org.spockframework.missing.ControllerIntegrationSpec

class ChangesetControllerIntegrationSpec extends ControllerIntegrationSpec {

    def setup() {
        //TODO this is dubious. Why would we need to change the marshaller for tests!?
        //TODO see: http://grails.1312388.n4.nabble.com/Testing-quot-render-x-as-XML-quot-with-mock-objects-td4632267.html
        JSON.registerObjectMarshaller(Changeset, { Changeset changeset -> [id: changeset.id, identifier: changeset.identifier] })
    }

    def 'should return few next changesets older than one with given revision id as JSON'() {
        given:
        String latestChangesetId = '3'
        buildChangelogEntry(latestChangesetId as Integer)
        buildChangelogEntry(2)
        buildChangelogEntry(1)

        when:
        controller.getNextFewChangesetsOlderThan(latestChangesetId, null)

        then:
        def responseChangesets = controller.response.json
        responseChangesets*.identifier == ['2', '1']
    }

    def 'getNextFewChangesetsOlderThan() should return few next changesets older one with given revision id as JSON'() {
        given:
        String latestChangesetId = '3'
        Project project = Project.build(name: 'foo')
        buildChangelogEntry(latestChangesetId as Integer, project)
        buildChangelogEntry(2, project)
        buildChangelogEntry(1, Project.build(name: 'bar'))
        buildChangelogEntry(0, project)

        when:
        controller.getNextFewChangesetsOlderThan(latestChangesetId, project.name)

        then:
        def responseChangesets = controller.response.json
        responseChangesets*.identifier == ['2', '0']
    }

    private void buildChangelogEntry(int positionCountingFromOldest, Project project = Project.build()) {
        Changeset.build(
                project: project,
                identifier: "$positionCountingFromOldest",
                date: minutesSinceEpoch(positionCountingFromOldest)
        )
    }

    //default time format is accurate to minutes, so for dates to be distinguishable in error logs, use this:
    private Date minutesSinceEpoch(int minutes) {
        new Date(minutes * 1000 * 60)
    }
}
