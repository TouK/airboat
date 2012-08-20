package codereview

import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor

@TestFor(Changeset)
@Build(Changeset)
class ProjectFileSpec extends Specification {

    def "Should get type of file from name of file" () {
        when:
        def files = ["userBupser/dhs/dd/alfa.cpp", "something.something.txt", "script.js" ].collect {
            new ProjectFile(name: it)
        }

        then:
        files[0].fileType == "cpp"
        files[1].fileType == "txt"
        files[2].fileType == "javascript"
    }
}
