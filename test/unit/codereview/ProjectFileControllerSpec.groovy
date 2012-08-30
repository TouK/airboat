package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import mixins.SpringSecurityControllerMethodsMock
import spock.lang.Ignore

@TestFor(ProjectFileController)
class ProjectFileControllerSpec  extends Specification {

    def setup() {
        controller.metaClass.mixin(SpringSecurityControllerMethodsMock)
    }

    def "should return true if file has a know text file format"() {
        when:
        def samples = ["py", "html", "png", "gsp", "groovy"]
        def correctAnswers = [true, true, false, true, true]
        then:
        samples.eachWithIndex { sample, index ->
            assert (controller.isKnownTextFormat(sample) == correctAnswers[index] )
        }
    }
}
