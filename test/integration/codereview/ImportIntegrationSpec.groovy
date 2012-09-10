package codereview

import grails.plugin.spock.IntegrationSpec

import testFixture.Fixture

import static testFixture.Fixture.getPROJECT_CODEREVIEW_NAME
import util.DbPurger

import static testFixture.Fixture.PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL

//FIXME this test takes ~30 seconds on my (superb) machine (without setup). Use a smaller repository (say: 50 commits)
class ImportIntegrationSpec extends IntegrationSpec {

    static GitRepositoryService gitRepositoryService
    static InfrastructureService infrastructureService

    static transactional = false

    static GString projectUrl = PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL

    ScmAccessService scmAccessService

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

    def "should import changesets and "() {
        when:
        Project.withNewSession {
            scmAccessService.importAllChangesets(projectUrl)
        }

        then:
        Project.findByName(PROJECT_CODEREVIEW_NAME).changesets.size() > Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
    }

}