package airboat

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture

import static airboat.Constants.AIRBOAT_WORKING_DIRECTORY_PROPERTY
import grails.buildtestdata.mixin.Build

@Build(Project)
class InfrastructureServiceSpec extends Specification {

    InfrastructureService service = new InfrastructureService()

    String workingDirectory

    def setup() {
        workingDirectory = System.getProperty(AIRBOAT_WORKING_DIRECTORY_PROPERTY)
    }

    def cleanup() {
        System.clearProperty(AIRBOAT_WORKING_DIRECTORY_PROPERTY)
        if (workingDirectory != null) {
            System.setProperty(AIRBOAT_WORKING_DIRECTORY_PROPERTY, workingDirectory)
        }
    }

    def 'should use airboat.workingDirectory parameter to determine base directory name'() {
        given:
        String overridenDirectory = "wherever-you-want"
        System.setProperty(AIRBOAT_WORKING_DIRECTORY_PROPERTY, overridenDirectory)

        when:
        def workindDirectory = service.getWorkingDirectory()

        then:
        workindDirectory == new File(overridenDirectory)
    }

    def 'temporary dir path should be returned for base directory name if not configured otherwise'() {
        given:
        System.clearProperty(AIRBOAT_WORKING_DIRECTORY_PROPERTY)

        when:
        def workindDirectory = service.getWorkingDirectory()

        then:
        workindDirectory == new File(System.getProperty('java.io.tmpdir'), 'airboat-work')
    }

    def 'getDirectoryNameForTheProject() should return project name'() {
        given:
        def testProject = Project.build()

        when:
        def returnProjectName = service.directoryNameForProject(testProject.url)

        then:
        returnProjectName == testProject.name
    }

}
