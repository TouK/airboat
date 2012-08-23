package codereview

import grails.plugin.spock.IntegrationSpec
import spock.lang.Ignore
import testFixture.Fixture
import org.eclipse.jgit.diff.DiffEntry

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    GitRepositoryService gitRepositoryService

    def "should extract project name from scmUrl"() {
        when:
        def projectName = gitRepositoryService.getProjectNameFromScmUrl(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def projectName2 = gitRepositoryService.getProjectNameFromScmUrl("git://home/projects/kaboom.git")

        then:
        projectName == "codereview"
        projectName2 == "kaboom"
    }

    @Ignore //FIXME implement
    def "should set repo  if it isn't set already"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        then:
        true
    }

    //FIXME this test has a weak verificaiton part, supposedly needs better setup remove already existing repo?
    def "should update repo by pulling"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        gitRepositoryService.updateRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        then:
        noExceptionThrown()
    }

    def "should get all changesets"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changesets = gitRepositoryService.getAllChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        then:
        !changesets.isEmpty()
        changesets.size > 0
        changesets[1].files != null
    }

    def "check changeset file ChangeType "() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changesets = gitRepositoryService.getAllChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        then:
        changesets[0].files[0].changeType == DiffEntry.ChangeType.MODIFY
    }

    def "should get only newer changesets"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changesets = gitRepositoryService.getAllChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        int index = changesets.size() / 10
        String hash = changesets[index].rev
        def newerChangesets = gitRepositoryService.getNewChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL, hash)

        then:
        !newerChangesets.isEmpty()
        newerChangesets.size() < changesets.size()
        newerChangesets.last().authorEmail
        newerChangesets.last().fullMessage
    }

    def "should get arbitrary changeset's file content"() {
        given:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        gitRepositoryService.updateRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL,
                Fixture.FIRST_COMMIT_HASH,
                Fixture.APPLICATION_PROPERTIES_FILE_NAME
        )
        def otherText = gitRepositoryService.getFileContentFromChangeset(
                Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL,
                Fixture.SECOND_COMMIT_INCLUDING_APPLICATION_PROPERTIES,
                Fixture.APPLICATION_PROPERTIES_FILE_NAME
        )

        then:
        text.split('\n')[1] == Fixture.APPLICATION_PROPERTIES_SECOND_LINE_IN_FIRST_COMMIT
        otherText.split('\n')[1] == Fixture.APPLICATION_PROPERTIES_SECOND_LINE_IN_SECOND_COMMIT_INCLUDING_IT
    }

    def "should get arbitrary changeset's file content for a file nested in subdirectories"() {
        given:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL,
                Fixture.FIRST_COMMIT_HASH,
                Fixture.PATH_TO_FILE_PRESENT_IN_FIRST_COMMIT
        )

        then:
        text.split('\n')[0] == Fixture.FIRST_LINE_OF_FILE_PRESENT_IN_FIRST_COMMIT
    }

    def "should get arbitrary changeset's file content with proper polish diacritic characters"() {
        given:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL,
                Fixture.FIRST_COMMIT_WITH_FILE_IN_PONGLISH,
                Fixture.FILE_IN_PONGLISH
        )

        then:
        text.split('\n')[Fixture.LINE_WITH_PONGLISH_NUMBER - 1] == Fixture.LINE_WITH_PONGLISH_TEXT
    }

    def "should throw an exception for inexistent commit"() {
        given:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL,
                "notAHashReally",
                Fixture.FILE_IN_PONGLISH
        )

        then:
        thrown(IllegalArgumentException)
    }

    def "should throw an exception for inexistent file"() {
        given:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        when:
        def text = gitRepositoryService.getFileContentFromChangeset(
                Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL,
                Fixture.FIRST_COMMIT_WITH_FILE_IN_PONGLISH,
                'no-such-file.txt'
        )

        then:
        thrown(IllegalArgumentException)
    }
}