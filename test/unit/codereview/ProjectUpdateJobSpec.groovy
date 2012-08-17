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
        def project = new Project('testProject', 'testUrl')
        def committer = new Commiter('agj')
        def changeset = new Changeset('hash23', 'coding', new Date())

        project.addToChangesets(changeset)
        committer.addToChangesets(changeset)

        project.save(failOnError: true)
        committer.save(failOnError: true)
        def job = new ProjectUpdateJob()
        job.scmAccessService = scmAccessServiceMock

        when:
        job.update(project)

        then:
        Changeset.count() != 0
    }
}
