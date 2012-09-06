package codereview

import grails.converters.JSON
import grails.plugin.spock.IntegrationSpec

class ChangesetControllerIntegrationSpec extends IntegrationSpec {

    def scmAccessService
    ChangesetController controller = new ChangesetController(scmAccessService: scmAccessService)

    def 'should return few next changesets older than one with given revision id as JSON'() {
        given:
        String latestChangesetId = '3'
        buildChangelogEntry(latestChangesetId as Integer)
        buildChangelogEntry(2)
        buildChangelogEntry(1)

        when:
        controller.getNextFewChangesetsOlderThan(latestChangesetId, null)

        then:
        def responseChangesets = controller.response.json.collect{day, changesetsForDay -> changesetsForDay}.flatten()
        responseChangesets*.identifier == ['2', '1']
    }

    def 'should return next few changesets older than given, within given project as JSON'() {
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
        def responseChangesets = controller.response.json.collect{day, changesetsForDay -> changesetsForDay}.flatten()
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
