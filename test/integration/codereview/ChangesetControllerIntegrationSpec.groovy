package codereview

import grails.plugin.spock.IntegrationSpec

class ChangesetControllerIntegrationSpec extends IntegrationSpec {

    def springSecurityService
    def scmAccessService
    ChangesetController controller = new ChangesetController(scmAccessService: scmAccessService)

    def 'getLastChangesets should return JSON'() {
        given:
        Project project = Project.build()
        def changesets = (1..3).collect { Changeset.build(project: project) }

        when:
        controller.params.projectName = project.name
        controller.getLastChangesets()

        then:
        controller.response.getContentType().startsWith('application/json')
        responseChangesets.size() == changesets.size()
    }

    def 'getChangesetFiles should return file names from changeset'() {
        given:
        Project project = Project.build()
        ProjectFile projectFile = ProjectFile.buildWithoutSave(name: 'kickass!', project: project)
        Changeset changeset = Changeset.build(project: project)
        ProjectFileInChangeset.build(changeset: changeset, projectFile: projectFile)

        when:
        controller.params.id = changeset.identifier
        controller.getChangesetFiles()

        then:
        controller.response.json.size() == 1
        controller.response.json.first().name == projectFile.name
    }

    def "should mark logged in user's changesets as theirs"() {
        given:
        def authenticatedUser = User.build(username: 'agj@touk.pl')
        springSecurityService.reauthenticate(authenticatedUser.username)
        def loggedInUsersCommitter = Commiter.build(user: controller.authenticatedUser)
        Changeset.build(commiter: loggedInUsersCommitter)
        Changeset.build(commiter: Commiter.build(user: User.build(username: 'kpt@touk.pl')))

        when:
        controller.getLastChangesets()

        then:
        responseChangesets*.belongsToCurrentUser == [false, true]
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

    private Collection<?> getResponseChangesets() {
        controller.response.json.collect {day, changesetsForDay -> changesetsForDay}.flatten()
    }
}
