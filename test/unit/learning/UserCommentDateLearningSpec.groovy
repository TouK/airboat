package learning

import spock.lang.Specification
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import codereview.UserCommentController
import codereview.UserComment
import codereview.Changeset
import codereview.Project

@TestFor(UserCommentController)
@Mock([Changeset, UserComment, Project])
class UserCommentDateLearningSpec extends Specification {

    def "dateCreated find by Dynamic Finder  should be != null"() {
        given:
        def testProject = new Project("testProject","testUrl")
        def testUserComment =  new  UserComment("kpt","text")
        testProject.addToChangesets(new Changeset("hash23", "agj", "zmiany", new Date())
                .addToUserComments(testUserComment))
        testProject.save()


        expect:
        UserComment.findByAuthorAndText("kpt","text").dateCreated != null

    }
    def "dateCreated find  normally way  should be != null"() {

        given:
        def testProject = new Project("testProject","testUrl")
        def testUserComment =  new  UserComment("kpt","text")
        def testChangeset =  new Changeset("hash23", "agj", "zmiany", new Date())
                .addToUserComments(testUserComment)
        testProject.addToChangesets(testChangeset)
        testProject.save()


        expect:
        testChangeset.userComments.author[0] == "kpt"
    }

}
