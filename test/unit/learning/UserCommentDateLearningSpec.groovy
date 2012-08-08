package learning

import codereview.Changeset
import codereview.Commiter
import codereview.UserComment
import codereview.UserCommentController
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(UserCommentController)
@Mock([Commiter, Changeset, UserComment])
@Build(UserComment)
class UserCommentDateLearningSpec extends Specification {

    def "dateCreated find by Dynamic Finder should be != null"() {
        given:
        def commentAuthor = "kpt"
        def comment = UserComment.build(author: commentAuthor)

        expect:
        UserComment.findByAuthorAndText(commentAuthor, comment.text).dateCreated != null
    }
}
