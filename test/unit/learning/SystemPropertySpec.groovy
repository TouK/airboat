package learning

import spock.lang.Specification

class SystemPropertySpec extends Specification {

    def "read system property"() {

        when:
            def result = System.getProperty("java.io.tmpdir")

        then:
            result == "/tmp"
    }

    def "read codereview.workingDirectory property"() {

        when:
            def result = System.getProperty("codereview.workingDirectory")

        then:
            result == null
    }
    def "should set the baseDir" () {
        when:
        def result = System.getProperty("codereview.workingDirectory")
        File baseDir
        if (result != null)     {
            baseDir = new File(result);
        }
        else {
            baseDir = new File(System.getProperty("java.io.tmpdir"));
        }
        then:
        result == null
        baseDir != null
    }




}
