package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture

@Mock([Commiter, Project, Changeset])
class ProjectUpdateJobSpec extends Specification {

    ScmAccessService scmAccessServiceMock

    def setup() {
        scmAccessServiceMock = Mock(ScmAccessService)
    }

    void "shouldn't delete all old changesets during updating"() {

        given:
        def project = new Project("testProject", "testUrl")
        def committer = new Commiter("agj")
        def changeset = new Changeset("hash23", "coding", new Date())

        project.addToChangesets(changeset)
        committer.addToChangesets(changeset)

        project.save()
        committer.save()
        def job = new ProjectUpdateJob()
        job.scmAccessService = scmAccessServiceMock

        when:
        job.update(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)

        then:
        Changeset.count() != 0
    }
}
