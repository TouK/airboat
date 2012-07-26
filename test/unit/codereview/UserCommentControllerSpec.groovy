package codereview


import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON
import spock.lang.Ignore

@TestFor(UserCommentController)
@Mock(UserComment)
class UserCommentControllerSpec extends Specification {           //TODO implement tests!

    @Ignore
    def "should return comments to changeset when given right changeset id"() {
        when:
        def something = "something"

        then:
        true
    }

    @Ignore
    def "should return last comments, sorted by dateCreated, descending"() {
         when:
         def something = "something"

         then:
         true
    }

    @Ignore
    def "should add comment correctly to db"() {
        when:
        def something = "something"

        then:
        true
    }


}