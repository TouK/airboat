package airboat

import grails.plugin.spock.IntegrationSpec

import static testFixture.Fixture.*

import org.eclipse.jgit.diff.DiffEntry
import testFixture.Fixture

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    static GitRepositoryService gitRepositoryService
    static InfrastructureService infrastructureService

    ScmAccessService scmAccessService
    static GString projectUrl = PROJECT_AIRBOAT_ON_THIS_MACHINE_URL

    def setupSpec() {
        assert infrastructureService.getWorkingDirectory().deleteDir()
        Project.build(name: PROJECT_AIRBOAT_NAME, url: projectUrl)
        gitRepositoryService.updateOrCheckOutRepository(projectUrl)
    }

    def cleanupSpec() {
        Project.findByName(PROJECT_AIRBOAT_NAME).delete(flush: true)
        infrastructureService.getWorkingDirectory().deleteDir()
    }

    def "should get all changesets"() {
        when:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl)

        then:
        !changesets.isEmpty()
        changesets.size > 0
        changesets[1].files != null
    }

    def "should get fifty changesets (last)"() {
        when:
        def changesets = gitRepositoryService.getLastChangesets(projectUrl, 50)

        then:
        changesets.size() == 50
    }

    def 'should get older changesets than given'() {
        when:
        def changesets = gitRepositoryService.getRestChangesets(projectUrl, Fixture.SECOND_COMMIT_INCLUDING_APPLICATION_PROPERTIES)

        then:
        changesets.size() == Fixture.SECOND_COMMIT_INCLUDINF_APPLICATION_PROPERTIES_NUMBER -1;
    }

    def "should get only newer changesets"() {
        given:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl, (int) Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS / 2)

        when:
        def newerChangesets = gitRepositoryService.getNewChangesets(projectUrl, changesets.last().rev)

        then:
        !newerChangesets.isEmpty()
        changesets.collect { it.date }.max() < newerChangesets.collect { it.date }.min()
    }

    def "should get arbitrary changeset's file content"() {
        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                projectUrl,
                FIRST_COMMIT_HASH,
                APPLICATION_PROPERTIES_FILE_NAME
        )
        def otherText = gitRepositoryService.getFileContentFromChangeset(
                projectUrl,
                SECOND_COMMIT_INCLUDING_APPLICATION_PROPERTIES,
                APPLICATION_PROPERTIES_FILE_NAME
        )

        then:
        text.split('\n')[1] == APPLICATION_PROPERTIES_SECOND_LINE_IN_FIRST_COMMIT
        otherText.split('\n')[1] == APPLICATION_PROPERTIES_SECOND_LINE_IN_SECOND_COMMIT_INCLUDING_IT
    }

    def "should get arbitrary changeset's file content for a file nested in subdirectories"() {
        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                projectUrl,
                FIRST_COMMIT_HASH,
                PATH_TO_FILE_PRESENT_IN_FIRST_COMMIT
        )

        then:
        text.split('\n')[0] == FIRST_LINE_OF_FILE_PRESENT_IN_FIRST_COMMIT
    }

    def "should get arbitrary changeset's file content with proper polish diacritic characters"() {
        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                projectUrl,
                FIRST_COMMIT_WITH_FILE_IN_PONGLISH,
                FILE_IN_PONGLISH
        )

        then:
        text.split('\n')[LINE_WITH_PONGLISH_NUMBER - 1] == LINE_WITH_PONGLISH_TEXT
    }

    def "should throw an exception for inexistent commit"() {
        when:
        gitRepositoryService.getFileContentFromChangeset(
                projectUrl,
                "notAHashReally",
                FILE_IN_PONGLISH
        )

        then:
        thrown(IllegalArgumentException)
    }

    def "should throw an exception for inexistent file"() {
        when:
        gitRepositoryService.getFileContentFromChangeset(
                projectUrl,
                FIRST_COMMIT_WITH_FILE_IN_PONGLISH,
                'no-such-file.txt'
        )

        then:
        thrown(IllegalArgumentException)
    }

    def "should get changesets in chronological order"() {
        when:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl)

        then:
        def changesetsInChronologicalOrder = changesets.sort(false) { it.date.time }
        changesets == changesetsInChronologicalOrder
    }

    def "should read changeset types from git repository"() {
        when:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl)

        then:
        def firstCommit = changesets.first()
        firstCommit.files.every { it.changeType == DiffEntry.ChangeType.ADD }
    }
}