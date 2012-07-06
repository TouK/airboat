package learning

import spock.lang.Specification

class SpockLearningSpec extends Specification {

    def "How to return a list from a Spock mock"() {
        Mocked mocked = Mock()
        def thing = "bar"
        def otherThing = "foo"
        given:
            mocked.getThings() >> [thing, otherThing]

        when:
            def result = mocked.getThings()

        then:
            result == [thing, otherThing]
            result.size() == 2
    }

    def "methods without spock labels (given, when, then, expect, etc) won't be run"() {
        false
    }
}
