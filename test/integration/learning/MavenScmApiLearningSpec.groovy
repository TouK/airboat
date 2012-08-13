package learning

import com.google.common.io.Files
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository
import org.apache.maven.scm.repository.ScmRepository
import spock.lang.Specification
import testFixture.Fixture

//FIXME these tests are bloody slow, especially over VPN. Prepare a test repository in test setup / additional build script, maybe?
class MavenScmApiLearningSpec extends Specification {

    def 'should get whole changelog from git'() {

        given:
        ScmFileSet allFilesInProject = new ScmFileSet(Files.createTempDir(), '*.*')
        def gitProviderRepository = new GitScmProviderRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def gitRepository = new ScmRepository('git', gitProviderRepository)
        def git = new GitExeScmProvider()

        when:
        git.checkOut(gitRepository, allFilesInProject)
        def changeLogScmResult = git.changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, 'master')

        then:
        def changelog = changeLogScmResult.getChangeLog().getChangeSets()
        isNOTSortedChronologically(changelog)
        changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS

        then:
        //TODO extract constants for first commit in this project
        def firstCommit = changelog.last()
        firstCommit.revision == Fixture.FIRST_COMMIT_HASH
        firstCommit.author == Fixture.FIRST_COMMIT_AUTHOR
        firstCommit.comment == Fixture.FIRST_COMMIT_COMMENT
        firstCommit.date == Fixture.FIRST_COMMIT_DATE
    }

    private boolean isNOTSortedChronologically(List<ChangeSet> changelog) {
        changelog != changelog.sort(false, { -it.date.time })
    }

    //TODO explore this class more, we encountered some surprising behaviour,
    //TODO how does it behave when asking for changelog for second or third time?
    //TODO how exactly does it look like, it's not explicit right now

}
