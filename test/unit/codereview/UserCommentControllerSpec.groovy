package codereview


import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON
import spock.lang.Ignore

@TestFor(UserCommentController)
@Mock([Changeset, UserComment])
class UserCommentControllerSpec extends Specification {           //TODO implement tests!

    def "should return comments to changeset when given right changeset id"() {
        given:

        new Changeset("hash23", "agj", "zmiany", new Date())
            .addToUserComments(new UserComment(text: "fajno", author: "jil@touk.pl"))
            .save()

        when:
        params.id = "hash23"
        controller.returnCommentsToChangeset()

        then:
        response.json.size() == 1
        response.json[0].text == "fajno"

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