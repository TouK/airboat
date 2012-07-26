package codereview

import spock.lang.Specification

import grails.test.mixin.Mock

@Mock(Changeset)
class ProjectUpdateJobSpec extends Specification {

    ScmAccessService scmAccessServiceMock

    def setup() {
        scmAccessServiceMock = Mock(ScmAccessService)
    }

    void "shouldn't delete all old changesets during updating"() {

        given:
            new Changeset("hash23", "coding", new Date()).save()
            def job = new ProjectUpdateJob()
            job.scmAccessService = scmAccessServiceMock

        when:
            job.update()

        then:
            Changeset.count() != 0
    }

    void "should import changesets during updating and not delete any of newley imported ones"() {

        given:
            def job = new ProjectUpdateJob()
            job.scmAccessService = scmAccessServiceMock
            1 * job.scmAccessService.importAllChangesets(_) >> { new Changeset("hash23", "coding", new Date()).save() }

        when:
            job.update()

        then:
            Changeset.count() != 0
    }
}
