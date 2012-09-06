package codereview

import grails.plugin.spock.IntegrationSpec

import static testFixture.Fixture.*
import testFixture.Fixture
import org.eclipse.jgit.diff.DiffEntry

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    static GitRepositoryService gitRepositoryService
    static InfrastructureService infrastructureService

    ScmAccessService scmAccessService
    static GString projectUrl = PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL

    def setupSpec() {
        assert infrastructureService.getWorkingDirectory().deleteDir()
        Project.build(name: PROJECT_CODEREVIEW_NAME, url: projectUrl)
        gitRepositoryService.updateOrCheckOutRepository(projectUrl)
    }

    def cleanupSpec() {
        Project.findByName(PROJECT_CODEREVIEW_NAME).delete(flush: true)
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

    def "should get only newer changesets"() {
        when:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl)
        int index = changesets.size() / 10
        String hash = changesets[index].rev
        def newerChangesets = gitRepositoryService.getNewChangesets(projectUrl, hash)

        then:
        !newerChangesets.isEmpty()
        newerChangesets.size() < changesets.size()
        newerChangesets.last().gitCommitterId
        newerChangesets.last().fullMessage
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

    def "should get changesets in reverse chronological order"() {
        when:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl)

        then:
        def changesetsInReverseChronology = changesets.sort(false) { -it.date.time }
        changesets == changesetsInReverseChronology
    }

    def "should read changeset types from git repository"() {
        when:
        def changesets = gitRepositoryService.getAllChangesets(projectUrl)

        then:
        def firstCommit = changesets.last()
        firstCommit.files.every { it.changeType == DiffEntry.ChangeType.ADD }
    }
}