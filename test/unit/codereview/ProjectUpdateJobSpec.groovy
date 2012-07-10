//package codereview
//

//FIXME: new test
//import grails.test.mixin.Mock
//import spock.lang.Specification
//import grails.test.mixin.TestFor
//
//@TestFor(ProjectUpdateJob)
//@Mock(Changeset)
//class ProjectUpdateJobSpec extends Specification {
//
//    def "Should do something"() {
//        given:
//
//        ProjectUpdateJob projectUpdateJobMock = Mock()
//        projectUpdateJobMock.execute() >> { new Changeset("flald", "agj", new Date()).save() }
//
//
//        when:
//        projectUpdateJobMock.execute()
//
//        then:
//        Changeset.count() != 0
//        1 == 2
//    }
//
//
//}