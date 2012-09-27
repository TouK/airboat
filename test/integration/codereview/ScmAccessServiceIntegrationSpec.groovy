package codereview

import grails.plugin.spock.IntegrationSpec

import org.springframework.dao.InvalidDataAccessApiUsageException

import util.DbPurger
import spock.lang.Ignore

//TODO maybe this spec tests too much. There's a service injected with two others underneath, a git repo is cloned...
class ScmAccessServiceIntegrationSpec extends IntegrationSpec {

    def springSecurityService

    static transactional = false

    def setup() {
        DbPurger.verifyDbIsClean()
    }

    def cleanup() {
        DbPurger.purgeDb()
        DbPurger.verifyDbIsClean()
    }

    def 'should fetch and save changesets in db'() {
        given:
        Project project
        def (changesetId, commitMessage, changesetAuthor) = ['id', 'comment', 'agj@touk.pl']
        Project.withNewSession {
            project = Project.build()
        }
        def scmAccessServiceWithMocks = prepareGitScmService(commitMessage, changesetAuthor, changesetId, project.url)

        when:
        Project.withNewSession {
            scmAccessServiceWithMocks.importChangesetsSinceBegining(project.url)
        }

        then:
        Changeset.count() == 1
        Changeset.findAllByIdentifierAndCommitMessage(changesetId, commitMessage).size() == 1
    }

    private prepareGitScmService(String commitMessage, String changesetAuthor, String changesetId, String url) {
        GitChangeset gitChangeset = new GitChangeset(commitMessage, changesetAuthor, changesetId, new Date())

        def scmAccessServiceWithMocks = new ScmAccessService()
        GitRepositoryService gitRepositoryService = Mock()
        1 * gitRepositoryService.getAllChangesets(url, _) >> [gitChangeset]

        scmAccessServiceWithMocks.gitRepositoryService = gitRepositoryService
        return scmAccessServiceWithMocks
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

        def scmAccessServiceWithMocks = prepareGitScmService('commitin', committer.cvsCommiterId, changeset.identifier, project.url)

        expect:
        Project.count() == 1
        Commiter.count() == 0
        Changeset.count() == 0
        changeset.commiter == committer

        when:
        Project.withNewSession {
            scmAccessServiceWithMocks.importChangesetsSinceBegining(project.url)
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
        Project project
        Commiter previouslySavedCommitter
        Project.withNewSession {
            project = Project.build()
            previouslySavedCommitter = Commiter.build(cvsCommiterId: 'Artur Gajowy <agj@touk.pl>')
            Changeset previousChangeset = Changeset.build(commiter: previouslySavedCommitter, project: project)
            ProjectFile projectFile = ProjectFile.build(project: project)
            ProjectFileInChangeset.build(changeset: previousChangeset, projectFile: projectFile)
        }
        def scmAccessServiceWithMocks = prepareGitScmService('commitin', previouslySavedCommitter.cvsCommiterId, commitId, project.url)

        when:
        Project.withNewSession {
            scmAccessServiceWithMocks.importChangesetsSinceBegining(project.url)
        }

        then:
        Commiter.count() == 1
        def commiter = Commiter.findByCvsCommiterId(previouslySavedCommitter.cvsCommiterId)
        commiter.id == previouslySavedCommitter.id
        commiter.changesets.size() == 2
        commiter.changesets.contains(Changeset.findByIdentifier(commitId))
    }

    @Ignore //FIXME make this pass
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

        def scmAccessServiceWithMocks = prepareGitScmService('commitin', cvsCommitterId, 'hash23', project.url)

        when:
        Project.withNewSession {
            scmAccessServiceWithMocks.importChangesetsSinceBegining(project.url)
        }

        then:
        Project.withNewSession {
            def user = User.findByEmail(email)
            def associatedCommiters = user.committers
            assert associatedCommiters*.cvsCommiterId == [cvsCommitterId]
        }
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
