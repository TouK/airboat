package codereview

import testFixture.Fixture

import grails.plugin.spock.IntegrationSpec

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    def gitRepositoryService
    def scmAccessService

    def "should fetch changesets from project's repository"() {

        when:
            gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
            def changelog = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)

        then:
            //changelog == changelog.sort(true, { it.date.time })    //TODO if not useful - remove
            changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
            changelog.last().author == Fixture.FIRST_COMMIT_AUTHOR
            changelog.last().date == Fixture.FIRST_COMMIT_DATE
    }

    def "should get the name of first changed file in codereview project"() {
        when:
            gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changedFiles = gitRepositoryService.getFileNamesFromChangeSetsList(changes)

        then:
            changedFiles[0][0]?.getName() != null    //TODO change validation of getting, we only know, that we got "something"!
    }

    //TODO add test for fetchFullChangelog when in case project was not been checked out
    def "should return getFiles"(){
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def change = changes[0]
            def changedFiles = gitRepositoryService.getFileNamesFromGitChangeSet(change)

        then:
            changedFiles.size() != 0
            changedFiles[0].getName() != null
    }

    def "should create changesets with added files" () {                //TODO, check if files are added correctly
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changesetsWithFiles = scmAccessService.createChangesetsWithFiles(changes)

        then:
            changesetsWithFiles !=  null
            changesetsWithFiles.size() == changes.size()                  //TODO add more validation
    }
}


