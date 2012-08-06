package codereview

import spock.lang.Specification

import grails.test.mixin.Mock
import testFixture.Constants

@Mock([Changeset, Project])
class ProjectUpdateJobSpec extends Specification {

    ScmAccessService scmAccessServiceMock

    def setup() {
        scmAccessServiceMock = Mock(ScmAccessService)
    }

    void "shouldn't delete all old changesets during updating"() {

        given:
            def testProject = new Project("testProject","testUrl")
            testProject.addToChangesets(  new Changeset("hash23", "agj", "", new Date()))
            testProject.save()
            def job = new ProjectUpdateJob()
            job.scmAccessService = scmAccessServiceMock

        when:
            job.update(Constants.PROJECT_REPOSITORY_URL)

        then:
            Changeset.count() != 0
    }

    void "should import changesets during updating and not delete any of newley imported ones"() {

        given:
            def testProject = new Project("testProject","testUrl")
            def job = new ProjectUpdateJob()
            job.scmAccessService = scmAccessServiceMock
            1 * job.scmAccessService.fetchAllChangesetsAndSave(_) >> {testProject.addToChangesets(new Changeset("hash23", "agj", "", new Date())).save() }

        when:
            job.update(Constants.PROJECT_REPOSITORY_URL)

        then:
            Changeset.count() != 0
    }
}
