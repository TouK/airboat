package codereview

import grails.plugin.spock.IntegrationSpec

import testFixture.Fixture

import static testFixture.Fixture.getPROJECT_CODEREVIEW_NAME
import util.DbPurger

import static testFixture.Fixture.PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL
import grails.plugins.springsecurity.SpringSecurityService

import static com.google.common.collect.Iterables.getOnlyElement
import org.hibernate.Session

//FIXME this test takes ~30 seconds on my (superb) machine (without setup). Use a smaller repository (say: 50 commits)
class ImportIntegrationSpec extends IntegrationSpec {

    static GitRepositoryService gitRepositoryService
    static InfrastructureService infrastructureService

    static transactional = false

    static GString projectUrl = PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL

    ScmAccessService scmAccessService
    SpringSecurityService springSecurityService
    LineCommentController lineCommentController = new LineCommentController()

    def setupSpec() {
        assert infrastructureService.getWorkingDirectory().deleteDir()
        Project.withNewSession { Session session ->
            Project.build(name: PROJECT_CODEREVIEW_NAME, url: projectUrl)
        }
        gitRepositoryService.updateOrCheckOutRepository(projectUrl)
        DbPurger.purgeDb()
    }

    def setup() {
        lineCommentController.scmAccessService = scmAccessService
        DbPurger.verifyDbIsClean()
        Project.withNewSession {
            Project.build(name: PROJECT_CODEREVIEW_NAME, url: projectUrl)
        }
    }

    def cleanup() {
        DbPurger.purgeDb()
        DbPurger.verifyDbIsClean()
    }

    def cleanupSpec() {
        infrastructureService.getWorkingDirectory().deleteDir()
    }

    def 'should import first changeset'() {
        when:
        Project.withNewSession {
            scmAccessService.importChangesetsSinceBegining(projectUrl, 1)
        }

        then:
        Changeset changeset = getOnlyElement(Changeset.all)
        changeset.project == Project.findByName(PROJECT_CODEREVIEW_NAME)
        changeset.projectFilesInChangeset.size() == 56
    }

    def 'should import changesets and copy comment thread positions to newer changesets'() {
        when:
        def commentedChangeset
        def commentedProjectFile
        Project.withNewSession {
            scmAccessService.importChangesetsSinceBegining(projectUrl, 1)
        }
        Project.withNewSession {
            springSecurityService.reauthenticate(User.build().username)
            commentedChangeset = Changeset.findByIdentifier(Fixture.FIRST_COMMIT_HASH)
            commentedProjectFile = ProjectFile.findByProjectAndName(commentedChangeset.project, Fixture.APPLICATION_PROPERTIES_FILE_NAME)
            lineCommentController.addComment(commentedChangeset.identifier, commentedProjectFile.id, 1, 'a comment')
        }
        Project.withNewSession {
            scmAccessService.importChangesetsSince(projectUrl, commentedChangeset.identifier, 12)
        }

        then:
        Project.findByName(PROJECT_CODEREVIEW_NAME).changesets.size() == 1 + 12
        LineComment.count() == 1
        ThreadPositionInFile.count() == 3
    }

}