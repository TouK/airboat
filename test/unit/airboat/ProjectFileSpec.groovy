package airboat

import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor

@TestFor(Changeset)
@Build([Changeset, ProjectFile])
class ProjectFileSpec extends Specification {

    def 'Should get type of file from name of file' () {
        when:
        def projectFile = ProjectFile.build()
        projectFile.name = name;

        then:
        projectFile.getFileType() == type

        where:
        name                          | type
        'userBupser/dhs/dd/alfa.cpp'  | 'cpp'
        'something.something.txt'     | 'txt'
        'script.js'                   | 'javascript'
    }

    def 'should return true when file has an extension that is known text file format'() {
        expect:
        ProjectFile.build(name: "foo.$extension").isTextFormat()

        where:
        extension << ['py', 'html', 'gsp', 'groovy', 'sh', 'less']
    }

    def 'should return false when file has not an extension that is a known text file format'() {
        expect:
        ProjectFile.build(name: "foo.$extension").isTextFormat() == false

        where:
        extension << ['png', 'img', 'pdf']
    }

}
