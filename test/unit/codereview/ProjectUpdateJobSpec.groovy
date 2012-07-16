package codereview

import spock.lang.Specification
import grails.test.mixin.TestFor
import grails.test.mixin.Mock

@Mock(Changeset)
class ProjectUpdateJobSpec extends Specification {

    ChangelogAccessService changelogAccessServiceMock

    def setup() {
        changelogAccessServiceMock = Mock(ChangelogAccessService)
    }

    void "shouldn't delete all old changesets during updating"() {

        given:
            new Changeset("hash23", "agj", new Date()).save()
            def job = new ProjectUpdateJob()
            job.changelogAccessService = changelogAccessServiceMock

        when:
            job.update()

        then:
            Changeset.count() != 0
    }

    void "should import changesets during updating and not delete any of newley imported ones"() {

        given:
            def job = new ProjectUpdateJob()
            job.changelogAccessService = changelogAccessServiceMock
            1 * job.changelogAccessService.fetchChangelogAndSave(_) >> { new Changeset("hash23", "agj", new Date()).save() }

        when:
            job.update()

        then:
            Changeset.count() != 0
    }
}
