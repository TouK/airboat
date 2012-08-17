package learning

import spock.lang.Specification

import codereview.GitRepositoryService
import spock.lang.Ignore

class JGitRepositoryCheckoutLearningSpec extends Specification {

    @Ignore
    def "should set up a new repo for me!" () {
        when:
        def jGitRepositoryService  = new GitRepositoryService()
        jGitRepositoryService.createRepository("https://github.com/joyent/node.git", "node")
        then:
        true
    }
}
