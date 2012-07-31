package codereview

import testFixture.Fixture

import grails.plugin.spock.IntegrationSpec
import org.apache.maven.scm.ChangeSet

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    def gitRepositoryService
    def scmAccessService

    def "should fetch changesets from project's repository"() {
        when:
            gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
            def changelog = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)

        then:
            changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
            changelog.findAll { ChangeSet changeSet -> (
                changeSet.date == Fixture.FIRST_COMMIT_DATE
                && changeSet.author == Fixture.FIRST_COMMIT_AUTHOR
                && changeSet.comment == Fixture.FIRST_COMMIT_COMMENT
            )}.size() == 1
    }

    //TODO add test for fetchFullChangelog when in case project was not been checked out

    def "should create changesets with added files" () {
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changesetsWithFiles = scmAccessService.convertToChangesets(changes)

        then:
            changesetsWithFiles !=  null
            changesetsWithFiles.size() == changes.size()
            changesetsWithFiles.each {
                assert(!it.projectFiles.isEmpty())
                assert(it.projectFiles.iterator().next().name != null)
                assert(it.projectFiles.iterator().next().name != "")
            }
    }

    def "Should do initial check out" () {

    }

    //TODO tests to be written:
    //TODO test initialCheckOut and updateProject methods if they work as we expect

}


