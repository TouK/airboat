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
            changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
            changelog.last().author == Fixture.FIRST_COMMIT_AUTHOR
            changelog.last().date == Fixture.FIRST_COMMIT_DATE
    }

    //TODO add test for fetchFullChangelog when in case project was not been checked out

    def "should create changesets with added files" () {                //TODO, check if files are added correctly
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changesetsWithFiles = scmAccessService.createChangesetsWithFiles(changes)

        then:
            changesetsWithFiles !=  null
            changesetsWithFiles.size() == changes.size()                  //TODO add more validation
    }
    //TODO tests to be written:
    //TODO test initialCheckOut and updateProject methods if they work as we expect

}


