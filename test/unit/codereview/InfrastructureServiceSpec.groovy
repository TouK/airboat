package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture

import static codereview.Constants.CODEREVIEW_WORKING_DIRECTORY_PROPERTY

@Mock(Project)
class InfrastructureServiceSpec extends Specification {

    String workingDirectory

    def setup() {
        workingDirectory = System.getProperty(CODEREVIEW_WORKING_DIRECTORY_PROPERTY)
    }

    def cleanup() {
        System.setProperty(CODEREVIEW_WORKING_DIRECTORY_PROPERTY, workingDirectory)
    }

    def 'should use codereview.workingDirectory parameter to determine base directory name'() {
        when:
        def overridenDirectory = "wherever-you-want"
        System.setProperty(CODEREVIEW_WORKING_DIRECTORY_PROPERTY, overridenDirectory)
        def name = new InfrastructureService().getBaseDirectoryName()

        then:
        name == overridenDirectory
    }

    def 'temporary dir path should be returned for base directory name if not configured otherwise'() {
        when:
        System.clearProperty(CODEREVIEW_WORKING_DIRECTORY_PROPERTY)
        def name = new InfrastructureService().getBaseDirectoryName()

        then:
        name == new File(System.getProperty('java.io.tmpdir'), 'codereview-work').getAbsolutePath()
    }

    def 'getDirectoryNameForTheProject() should return project name'() {
        given:
        def testProject = new Project('Project', Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL).save()

        when:
        def returnProjectName = new InfrastructureService().getDirectoryNameForTheProject(testProject.url)

        then:
        returnProjectName == 'Project'
    }

}
