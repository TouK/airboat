package learning

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository
import org.apache.maven.scm.repository.ScmRepository
import spock.lang.Specification
import testFixture.Fixture
import com.google.common.io.Files

class MavenScmApiLearningSpec extends Specification {

    def "should get whole changelog from git"() {

        given:
            ScmFileSet allFilesInProject = new ScmFileSet(Files.createTempDir(), "*.*")
            //FIXME use testFixture.Fixture.PROJECT_REPOSITORY_URL and make it work under Grails build
            def gitProviderRepository = new GitScmProviderRepository("git@git.touk.pl:touk/codereview.git")
            def gitRepository = new ScmRepository("git", gitProviderRepository)
            def git = new GitExeScmProvider()

        when:
            git.checkOut(gitRepository, allFilesInProject)
            def changeLogScmResult = git.changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, "master")

        then:
            def changelog = changeLogScmResult.getChangeLog().getChangeSets()
            changelog == changelog.sort(false, { -it.date.time })
            changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS

        then:
            //TODO extract constants for first commit in this project
            def firstCommit = changelog.last()
            firstCommit.revision == Fixture.FIRST_COMMIT_HASH
            firstCommit.author == Fixture.FIRST_COMMIT_AUTHOR
            firstCommit.comment == Fixture.FIRST_COMMIT_COMMENT
            firstCommit.date == Fixture.FIRST_COMMIT_DATE
    }
    //TODO explore this class more, we encountered some surprising behaviour,
    //TODO how does it behave when asking for changelog for second or third time?
    //TODO how exactly does it look like, it's not explicit right now

}
