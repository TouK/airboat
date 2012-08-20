package learning

import spock.lang.Specification

import codereview.GitRepositoryService
import spock.lang.Ignore
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.api.Git

class JGitRepositoryCheckoutLearningSpec extends Specification {

    @Ignore
    def "should set up a new repo for me!" () {
        when:
        def jGitRepositoryService  = new GitRepositoryService()
        String PATH = "/tmp/codereviewRepos/node1"
        def scmUrl ="https://github.com/joyent/node.git"
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(PATH))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        Git git = new Git(repository)
        git.cloneRepository()
                .setBare(false)
                .setCloneAllBranches(true)
                .setDirectory(new File(PATH)).setURI(scmUrl)
                .call()

        then:
        true
    }
}
