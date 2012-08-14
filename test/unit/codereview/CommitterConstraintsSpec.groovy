package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Commiter)
@Build([Commiter, User])
class CommitterConstraintsSpec extends Specification {

    static String alreadyUserdCvsCommitterId = 'agj@touk.pl'

    def setup() {
        mockForConstraintsTests(Commiter)
        Commiter.build(cvsCommiterId: alreadyUserdCvsCommitterId)
    }

    @Unroll("Field '#field' of class Committer should have constraint '#constraint' violated by value '#violatingValue'")
    def 'Committer should have well defined constraints:'() {

        when:
        def committer = new Commiter("$field": violatingValue)

        then:
        committer.validate() == false
        committer.errors[field].toString() == constraint

        where:
        field           | constraint | violatingValue
        'cvsCommiterId' | 'blank'    | ''
        'cvsCommiterId' | 'unique'   | alreadyUserdCvsCommitterId
        'cvsCommiterId' | 'nullable' | null
    }

    def 'should be vaild without a user'() {
        when:
        Commiter committer = Commiter.build()

        then:
        committer.validate() == true
        committer.user == null
    }
}
