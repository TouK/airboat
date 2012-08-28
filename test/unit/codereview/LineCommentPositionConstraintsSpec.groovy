package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(LineCommentPosition)
@Build([LineCommentPosition])
class LineCommentPositionConstraintsSpec extends Specification {

    def setup() {
        mockForConstraintsTests(LineCommentPosition)
    }

    @Unroll("Field '#field' of class LineCommentPosition should have constraint '#constraint' violated by value '#violatingValue'")
    def 'LineCommentPosition should have well defined constraints:'() {
        when:
        def lineCommentPosition = new LineCommentPosition("$field": violatingValue)

        then:
        lineCommentPosition.validate() == false
        lineCommentPosition.errors[field].toString() == constraint

        where:
        field         | constraint | violatingValue
        'changeset'   | 'nullable' | null
        'projectFile' | 'nullable' | null
        'comment'     | 'nullable' | null
    }

    def "should allow null value for lineNumber"() {
        given:
        LineCommentPosition lineCommentPosition = LineCommentPosition.build()

        expect:
        lineCommentPosition.validate()
        lineCommentPosition.lineNumber == null
    }


    def "tests below should be implemented before pushing this to master"() {
        expect:
        false

        //FIXME implement the tests below
        /*
        one cannot add a comment in changeset other than newest
        comments should not be visible in revisions older than comment addition revision
        comments are shown in newly imported changests' files

        one comment can be associated with given pair of (project file, changeset) only once (?)
        one comment can be associated only with one project file (? - should be verified when we support file copy awareness)
        line comments in newly added changesets should have same line number as in last changeset for their project file
        line comments in newly added changesets should have null line number if it's undefined in new version of thier file
        */
    }
}
