package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(ThreadPositionInFile)
@Build([ThreadPositionInFile])
class ThreadPositionInFileConstraintsSpec extends Specification {

    def setup() {
        mockForConstraintsTests(ThreadPositionInFile)
    }

    @Unroll("Field '#field' of class ThreadPositionInFile should have constraint '#constraint' violated by value '#violatingValue'")
    def 'ThreadPositionInFile should have well defined constraints:'() {
        when:
        def threadPositionInFile = buildThreadPositionInFile()
        threadPositionInFile."$field" = violatingValue

        then:
        threadPositionInFile.validate() == false
        threadPositionInFile.errors[field].toString() == constraint

        where:
        field                    | constraint | violatingValue
        'projectFileInChangeset' | 'nullable' | null
        'thread'                 | 'nullable' | null
    }

    def "should allow null value for lineNumber"() {
        given:
        ThreadPositionInFile lineCommentPosition = buildThreadPositionInFile()

        expect:
        lineCommentPosition.validate()
        lineCommentPosition.lineNumber == null
    }

    def "one comment thread can be associated with a pair (changeset, projectFile) only once"() {
        given:
        ThreadPositionInFile existingPosition = buildThreadPositionInFile()

        when:
        ThreadPositionInFile invalidPosition = ThreadPositionInFile.buildWithoutSave(
                projectFileInChangeset: existingPosition.projectFileInChangeset,
                thread: existingPosition.thread
        )

        then:
        invalidPosition.validate() == false
        invalidPosition.errors.getFieldError('projectFileInChangeset').code == 'unique'
    }

    ThreadPositionInFile buildThreadPositionInFile() {
        ThreadPositionInFile.build()
    }

    def "tests below should be implemented before pushing this to master"() {
        expect:
        false

        //FIXME implement the tests below
        /*
        one cannot add a comment in changeset other than newest (add a thread in changeset other than newest)
        one can reply to a comment in changeset older than newest (add a comment to exisitng thread)

        comments should not be visible in revisions older than comment addition revision
            (when importing new changesets, only newly imported changesets get a ThreadPosition)
        comments are shown in newly imported changests' files

        one comment can be associated only with one project file (? - should be verified when we support file copy awareness)
        ThreadPositionInFile-s for newly added changesets should have same line number as in last changeset for their project file
        ThreadPositionInFile-s for newly added changesets should have null line number if it's undefined in new version of thier file
        */
    }
}
