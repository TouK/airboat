package codereview

import grails.plugin.spock.IntegrationSpec

import testFixture.Fixture

import static testFixture.Fixture.getPROJECT_CODEREVIEW_NAME
import util.DbPurger

import static testFixture.Fixture.PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL
import grails.plugins.springsecurity.SpringSecurityService

import static com.google.common.collect.Iterables.getOnlyElement

//FIXME this test takes ~30 seconds on my (superb) machine (without setup). Use a smaller repository (say: 50 commits)
class ImportIntegrationSpec extends IntegrationSpec {

    static GitRepositoryService gitRepositoryService
    static InfrastructureService infrastructureService

    static transactional = false

    static GString projectUrl = PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL

    ScmAccessService scmAccessService
    SpringSecurityService springSecurityService
    LineCommentController lineCommentController = new LineCommentController(scmAccessService: scmAccessService)

    def setup() {
        DbPurger.verifyDbIsClean()
        Project.withNewSession {
            assert infrastructureService.getWorkingDirectory().deleteDir()
            Project.build(name: PROJECT_CODEREVIEW_NAME, url: projectUrl)
            gitRepositoryService.updateOrCheckOutRepository(projectUrl)
        }
    }

    def cleanup() {
        infrastructureService.getWorkingDirectory().deleteDir()
        DbPurger.purgeDb()
        DbPurger.verifyDbIsClean()
    }

    def 'should import first changeset'() {
        when:
        Project.withNewSession {
            scmAccessService.importChangesetsSinceBegining(projectUrl, 1)
        }

        then:
        Project.withNewSession {
            Changeset changeset = getOnlyElement(Changeset.all)
            changeset.project = Project.findByName(PROJECT_CODEREVIEW_NAME)
            changeset.projectFilesInChangeset.size() == 56
        }
    }

    def "should import changesets and copy comment thread positions to newer changesets"() {
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
            lineCommentController.addComment(commentedChangeset, commentedProjectFile, 1, 'a comment')
        }
        Project.withNewSession {
            scmAccessService.importChangesetsSince(projectUrl, commentedChangeset.identifier, 12)
        }

        then:
        Project.findByName(PROJECT_CODEREVIEW_NAME).changesets.size() == 1 + 12
        LineComment.count() == 1
        ThreadPositionInFile.count() == 3
        ThreadPositionInFile.all*.projectFileInChangeset*.projectFile.unique() == [commentedProjectFile]
    }

}