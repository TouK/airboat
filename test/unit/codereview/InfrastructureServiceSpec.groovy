package codereview

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class InfrastructureServiceSpec extends Specification {

    def "temporary dir path should be returned for base directory name"() {
        given:

        when:
            def name = new InfrastructureService().getBaseDirectoryName()

        then:
            name == "/tmp"
    }

    //TODO later on name will be generated
    //FIXME change this test to test getDirectoryNameForTheProject
//    def "directory name for a project should be constant"() {
//        when:
//            def name = new InfrastructureService().getDirectoryNameForTheProject("abc")
//
//        then:
//            name == "projekt"
//    }
}
