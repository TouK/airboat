package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Changeset)
@Build([Changeset, ProjectFile])
class ChangesetConstraintsSpec extends Specification {


    def setup() {
        mockForConstraintsTests(Changeset)
    }

    @Unroll("Field '#field' of class Changeset should have constraint '#constraint' violated by value '#violatingValue'")
    def 'Changeset should have well defined constraints:'() {

        when:
        def changeset = Changeset.build()
        changeset."$field" = violatingValue

        then:
        changeset.validate() == false
        changeset.errors[field].toString() == constraint

        where:
        field           | constraint | violatingValue
        'identifier'    | 'blank'    | ''
        'identifier'    | 'nullable' | null
        'commitMessage' | 'nullable' | null
        'date'          | 'nullable' | null
    }

    def 'commentsCount should be zero (not null) for a Changeset without UserComment-s'() {
        when:
        Changeset changeset = Changeset.build()

        then:
        changeset.userComments == null
        changeset.commentsCount == 0
    }

    def 'two Changeset-s in two Projects can have the same identifier'() {
        given:
        Changeset existingChangeset = Changeset.build()

        expect:
        Changeset.build(identifier: existingChangeset.identifier)
    }

    def 'two Changeset-s in one Project can not have the same identifier'() {
        given:
        Changeset existingChangeset = Changeset.build()

        when:
        Changeset changeset = Changeset.buildWithoutSave(
                project: existingChangeset.project, identifier: existingChangeset.identifier
        )

        then:
        changeset.validate() == false
        changeset.errors.getFieldError('identifier').code == 'unique'
    }
}
