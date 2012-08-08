package codereview

import grails.plugin.spock.IntegrationSpec
import org.apache.maven.scm.ChangeSet
import spock.lang.Ignore
import testFixture.Fixture

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    def gitRepositoryService
    def scmAccessService

    def "should fetch changesets from project's repository"() {
        given:
        Project project = Project.build(url: Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        gitRepositoryService.checkoutProject(project.url)

        when:
        def changelog = gitRepositoryService.getAllChangeSets(project.url)

        then:
        changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
        changelog.findAll { ChangeSet changeSet ->
            (
            changeSet.date == Fixture.FIRST_COMMIT_DATE
                    && changeSet.author == Fixture.FIRST_COMMIT_AUTHOR
                    && changeSet.comment == Fixture.FIRST_COMMIT_COMMENT
            )
        }.size() == 1
    }

    //TODO add test for fetchFullChangelog when in case project was not been checked out

    def "should create changesets with added files"() {
        given:
        Project project = Project.build(url: Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        gitRepositoryService.checkoutProject(project.url)

        when:
        def changes = gitRepositoryService.getAllChangeSets(project.url)

        then:
        changes?.isEmpty() == false
        changes.each {
            assert !it.getFiles().isEmpty()
        }
    }

    @Ignore //FIXME implement test
    def "Should do initial check out"() {

    }

    //TODO tests to be written:
    //TODO test initialCheckOut and updateProject methods if they work as we expect

}


