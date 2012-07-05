package codereview

import spock.lang.Specification
import testFixture.Fixture

class GitRepositorySpec extends Specification {

    def "should fetch changesets from this project's repository"() {
        when:
        //FIXME use testFixture.Fixture.PROJECT_REPOSITORY_URL and make it work under Grails build
        def changelog = new GitRepository().fetchChangelog("git@git.touk.pl:touk/codereview.git")

        then:
        changelog == changelog.sort(false, { it.date.time })
        changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
        changelog.first().author == Fixture.FIRST_COMMIT_AUTHOR
        changelog.first().date == Fixture.FIRST_COMMIT_DATE
    }
}
