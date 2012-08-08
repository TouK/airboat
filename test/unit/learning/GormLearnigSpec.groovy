package learning

import spock.lang.Ignore
import spock.lang.Specification
import codereview.User
import codereview.Commiter
import grails.buildtestdata.mixin.Build

@Build([User, Commiter])
class GormLearnigSpec extends Specification {

    def "should set the one-side (User) in many-side (Changeset) when creating a bidirectional many-to-one relation"() {
        given:
        User user = User.build()
        Commiter commiter = Commiter.build()

        expect:
        commiter.user == null

        when:
        user.addToCommitters(commiter)

        then:
        commiter.user == user
        commiter.save()
        user.save()
    }
}
