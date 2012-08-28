package codereview

import grails.plugin.spock.IntegrationSpec

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.dao.InvalidDataAccessApiUsageException
import spock.lang.Ignore
import testFixture.Fixture
import org.eclipse.jgit.diff.DiffEntry

//TODO maybe this spec tests too much. There's a service injected with two others underneath, a git repo is cloned...
class ScmAccessServiceIntegrationSpec extends IntegrationSpec {

    def scmAccessService
    def springSecurityService

    static transactional = false

    def setup() {
        verifyDbIsClean()
    }

    def cleanup() {
        purgeDb()
        verifyDbIsClean()
    }

    static void verifyDbIsClean() {
        Project.withNewSession {
            domainClasses().each {
                if (it.count() != 0) {
                    throw new IllegalStateException("Db is not clean - ${it}.count() is ${it.count()}")
                }
            }
        }
    }

    private static List<Class<?>> domainClasses() {
        ApplicationHolder.application.domainClasses*.clazz
    }

    static void purgeDb() {
        Project.withNewSession {
            Project.all.each { Project project ->
                project.delete(flush: true)
            }
            User.all.each { User user ->
                user.committers*.user = null
                user.committers = [] as Set
                user.delete(flush: true)
            }
            Commiter.all.each {
                it.delete(flush: true)
            }
        }
    }

    def 'should fetch and save changesets in db'() {
        given:
        Project project
        def (changesetId, commitComment, changesetAuthor) = ['id', 'comment', 'agj@touk.pl']
        Project.withNewSession {
            project = Project.build()
        }
        prepareGitScmService(commitComment, changesetAuthor, changesetId, project.url)

        when:
        Project.withNewSession {
            scmAccessService.importAllChangesets(project.url)
        }

        then:
        Changeset.count() == 1
        Changeset.findAllByIdentifierAndCommitComment(changesetId, commitComment).size() == 1
    }

    private void prepareGitScmService(String commitComment, String changesetAuthor, String changesetId, String url) {
        GitChangeset gitChangeset = new GitChangeset(commitComment, changesetAuthor, changesetId, new Date())

        GitRepositoryService gitRepositoryService = Mock()
        1 * gitRepositoryService.getAllChangesets(url) >> [gitChangeset]

        scmAccessService.gitRepositoryService = gitRepositoryService
    }


    def 'should convert git ChangeSet to ChangeSet and add commiter'() {
        given:
        GitChangeset jGitChangeset = new GitChangeset('Refactoring', "jil@touk.pl", "someday123", new Date())

        when:
        def changeset = scmAccessService.buildChangeset(jGitChangeset)
        def changesetCommiter = changeset.commiter

        then:
        changeset != null
        changesetCommiter != null
    }

    def 'should save Changeset to db and add commiter if it is not yet in db'() {
        Project project
        Commiter committer
        Changeset changeset

        given:
        Project.withNewSession {
            project = Project.build()
            committer = Commiter.buildWithoutSave(cvsCommiterId: 'jil <jil@touk.pl>')
            changeset = Changeset.buildWithoutSave(project: project, commiter: committer)
        }

        prepareGitScmService('commitin', committer.cvsCommiterId, changeset.identifier, project.url)

        expect:
        Project.count() == 1
        Commiter.count() == 0
        Changeset.count() == 0
        changeset.commiter == committer

        when:
        Project.withNewSession {
            scmAccessService.importAllChangesets(project.url)
        }

        then:
        Commiter.count() == 1
        def committerFromDb = Commiter.findByCvsCommiterId(committer.cvsCommiterId)
        committerFromDb.changesets.size() == 1
        committerFromDb.changesets.contains(Changeset.findByIdentifier(changeset.identifier))
    }


    def 'should add a changeset for existing committer when saving changeset by them'() {
        given:
        String commitId = 'hash23'
        Project project = Project.build()
        Commiter previouslySavedCommitter
        Project.withNewSession {
            previouslySavedCommitter = Commiter.build(cvsCommiterId: 'Artur Gajowy <agj@touk.pl>')
            Changeset previousChangeset = Changeset.build(commiter: previouslySavedCommitter)
        }
        prepareGitScmService('commitin', previouslySavedCommitter.cvsCommiterId, commitId, project.url)

        when:
        Project.withNewSession {
            scmAccessService.importAllChangesets(project.url)
        }

        then:
        Commiter.count() == 1
        def commiter = Commiter.findByCvsCommiterId(previouslySavedCommitter.cvsCommiterId)
        commiter.id == previouslySavedCommitter.id
        commiter.changesets.size() == 2
        commiter.changesets.contains(Changeset.findByIdentifier(commitId))
    }


    def 'should associate Changeset with corresponding user'() {
        given:
        def email = 'agj@touk.pl'
        def cvsCommitterId = email
        Project project

        Project.withNewSession {
            project = Project.build()
            def user = new User(email, 'dupa.8')
            user.springSecurityService = springSecurityService
            user.save()
        }

        prepareGitScmService('commitin', cvsCommitterId, 'hash23', project.url)

        when:
        Changeset.withNewSession {
            scmAccessService.importAllChangesets(project.url)
        }

        then:
        email == 'agj@touk.pl'
        def associatedCommiters = User.findByEmail(email).committers
        associatedCommiters*.cvsCommiterId == [cvsCommitterId]
    }

    //learning tests. TODO move to another class. These tests will fail in unit-test environment. Document it.
    def "should not validate if subobjects don't validate"() {
        when:
        Commiter committer = new Commiter('valid id').addToChangesets(new Changeset(null, null, null))

        then:
        committer.validate() == false
    }

    def 'should throw an exception when using a transient association search key'() {
        when:
        Changeset.findByCommiter(new Commiter('kaboom'))

        then:
        thrown(InvalidDataAccessApiUsageException)
    }
}
