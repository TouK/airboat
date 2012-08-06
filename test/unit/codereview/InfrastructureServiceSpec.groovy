package codereview

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import testFixture.Fixture

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@Mock(Project)
class InfrastructureServiceSpec extends Specification {

    def "temporary dir path should be returned for base directory name"() {
        given:

        when:
            def name = new InfrastructureService().getBaseDirectoryName()

        then:
            name == "/tmp/codereview-work"
    }

    def "getDirectoryNameForTheProject() should return project name"() {
        given:
        def testProject = new Project("Project",Fixture.PROJECT_REPOSITORY_URL).save()

        when:
        def returnProjectName = new InfrastructureService().getDirectoryNameForTheProject(testProject.url)

        then:
        returnProjectName == "Project"


    }

}
