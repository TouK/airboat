package learning

import spock.lang.Specification
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import codereview.UserCommentController
import codereview.UserComment
import codereview.Changeset
import codereview.Commiter

@TestFor(UserCommentController)
@Mock([Commiter, Changeset, UserComment])
class UserCommentDateLearningSpec extends Specification {

    def "dateCreated find by Dynamic Finder should be != null"() {
        given:
//        def commiter = new Commiter("agj@touk.pl")
//        def testChangeset = new Changeset("hash23", "zmiany", new Date())
//        def testUserComment = new UserComment("kpt", "text")

        commiter.addToChangesets(testChangeset)
        testChangeset.addToUserComments(testUserComment)

        commiter.save()

        expect:
        UserComment.findByAuthorAndText("kpt","text").dateCreated != null
    }

    def "dateCreated find normally should be != null"() {
        given:
//        def commiter = new Commiter("agj@touk.pl")
//        def testChangeset = new Changeset("hash23", "zmiany", new Date())
//        def testUserComment = new UserComment("kpt", "text")

        commiter.addToChangesets(testChangeset)
        testChangeset.addToUserComments(testUserComment)

        commiter.save()

        expect:
        testChangeset.userComments.author[0] == "kpt"
    }

}
