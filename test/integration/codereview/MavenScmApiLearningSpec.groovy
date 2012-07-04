package codereview

import spock.lang.Specification

import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.repository.ScmRepository
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository

import org.apache.maven.scm.ScmFileSet

class MavenScmApiLearningSpec extends Specification {

    def "should get whole changelog from git"() {

        given:
            ScmFileSet allFilesInProject = new ScmFileSet(new File("."), "*.*")
            def gitProviderRepository = new GitScmProviderRepository("git@git.touk.pl:touk/codereview.git")
            def gitRepository = new ScmRepository("git", gitProviderRepository)
            def git = new GitExeScmProvider()

        when:
            def changeLogScmResult = git
                    .changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, "master")

        then:
            def changelog = changeLogScmResult.getChangeLog()
            changelog.getChangeSets().size() >= 2

        then:
            def firstCommit = changelog.getChangeSets().last()
            firstCommit.getAuthor() == "Kacper Pietrasik <kpt@touk.pl>"
            firstCommit.getComment() == "initial commit"
    }

}
