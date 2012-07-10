package codereview

import testFixture.Fixture

import grails.plugin.spock.IntegrationSpec

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    def gitRepositoryService

    def "should fetch changesets from project's repository"() {

        when:
            gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
            def changelog = gitRepositoryService.fetchFullChangelog(Fixture.PROJECT_REPOSITORY_URL)

        then:
            changelog == changelog.sort(false, { it.date.time })
            changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
            changelog.first().author == Fixture.FIRST_COMMIT_AUTHOR
            changelog.first().date == Fixture.FIRST_COMMIT_DATE
    }

    //TODO add test for fetchFullChangelog when in case project was not been checked out
}


