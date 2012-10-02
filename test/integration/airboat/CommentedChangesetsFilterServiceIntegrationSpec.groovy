package airboat

import grails.plugin.spock.IntegrationSpec
import grails.plugins.springsecurity.SpringSecurityService
import testFixture.Fixture

import org.hibernate.Session

import util.DbPurger

class CommentedChangesetsFilterServiceIntegrationSpec extends IntegrationSpec {

    CommentedChangesetsFilterService commentedChangesetsFilterService
    static GitRepositoryService gitRepositoryService
    static InfrastructureService infrastructureService

    static transactional = false

    static GString projectUrl = Fixture.PROJECT_AIRBOAT_ON_THIS_MACHINE_URL

    ScmAccessService scmAccessService
    SpringSecurityService springSecurityService
    LineCommentController lineCommentController = new LineCommentController()

    def setupSpec() {
        assert infrastructureService.getWorkingDirectory().deleteDir()
        Project.withNewSession { Session session ->
            Project.build(name: Fixture.PROJECT_AIRBOAT_NAME, url: projectUrl)
        }
        gitRepositoryService.updateOrCheckOutRepository(projectUrl)
        DbPurger.purgeDb()
    }

    def setup() {
        lineCommentController.scmAccessService = scmAccessService
        DbPurger.verifyDbIsClean()
        Project.withNewSession {
            Project.build(name: Fixture.PROJECT_AIRBOAT_NAME, url: projectUrl)
        }
    }

    def cleanup() {
        DbPurger.purgeDb()
        DbPurger.verifyDbIsClean()
    }

    def cleanupSpec() {
        infrastructureService.getWorkingDirectory().deleteDir()
    }

    def "should return only newest revisions of commented files"() {
        given:
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
            scmAccessService.importChangesetsSince(projectUrl, commentedChangeset.identifier, Fixture.SECOND_COMMIT_INCLUDINF_APPLICATION_PROPERTIES_NUMBER - 1)
        }

        when:
        def changesets = commentedChangesetsFilterService.getLastFilteredChangesets()

        then:
        !changesets.contains(Changeset.findByIdentifier(Fixture.FIRST_COMMIT_HASH))
        changesets.contains(Changeset.findByIdentifier(Fixture.SECOND_COMMIT_INCLUDING_APPLICATION_PROPERTIES))

    }


}
