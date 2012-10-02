package learning

import spock.lang.Ignore
import spock.lang.Specification
import airboat.User
import airboat.Commiter
import grails.buildtestdata.mixin.Build
import airboat.UserComment
import airboat.UnitTestBuilders

@Build([User, Commiter, UserComment])
class GormLearnigSpec extends Specification {

    def 'should set the one-side (User) in many-side (Changeset) when creating a bidirectional many-to-one relation'() {
        given:
        User user = UnitTestBuilders.buildUserWithIsDirtyMock()
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

    def 'should fill dateCreated field (also in unit test) upon save'() {
        given:
        User user = UnitTestBuilders.buildUserWithIsDirtyMock()
        UserComment comment = UserComment.build(author: user)

        expect:
        UserComment.findByAuthorAndText(comment.author, comment.text).dateCreated != null
    }

}
