package codereview

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Changeset)
class ChangesetConstraintsSpec extends Specification {

    static String alreadyUsedIdentifier = "alreadyUsedIdentifier"

    def setup() {
        mockForConstraintsTests(Changeset, [new Changeset(alreadyUsedIdentifier, "agj", "", new Date())])
    }

    @Unroll("Field '#field' of class Changeset should have constraint '#constraint' violated by value '#violatingValue'")
    def "Changeset should have well defined constraints:" () {

        when:
            def changeset = new Changeset("$field": violatingValue)

        then:
            changeset.validate() == false
            changeset.errors[field].toString() == constraint

        where:
            field           | constraint    | violatingValue
            'identifier'    | 'blank'       | ""
            'identifier'    | 'unique'      | alreadyUsedIdentifier
            'author'        | 'blank'       | ""
            'identifier'    | 'nullable'    | null
            'author'        | 'nullable'    | null
            'date'          | 'nullable'    | null
    }

    def "Changeset author does not have to have an email"() {
        when:
            def changeset = new Changeset("hash23", "Email-less Author", "", new Date())

        then:
            changeset.email == null
            changeset.validate() == true
    }
}
