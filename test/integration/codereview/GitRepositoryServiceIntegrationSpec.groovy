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

    def "should create changesets with added files" () {
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changesetsWithFiles = scmAccessService.convertToChangesets(changes)

        then:
            changesetsWithFiles !=  null
            changesetsWithFiles.size() == changes.size()
            changesetsWithFiles[0].author == Fixture.FIRST_COMMIT_AUTHOR
            changesetsWithFiles[0].commitComment == Fixture.FIRST_COMMIT_COMMENT
            changesetsWithFiles[0].identifier == Fixture.FIRST_COMMIT_HASH
            changesetsWithFiles.each {
                assert(!it.projectFiles.isEmpty())
                assert(it.projectFiles.iterator().next().name != null)
                assert(it.projectFiles.iterator().next().name != "")
            }
    }


    //TODO tests to be written:
    //TODO test initialCheckOut and updateProject methods if they work as we expect

}


