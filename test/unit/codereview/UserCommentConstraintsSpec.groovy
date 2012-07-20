package codereview


import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(UserComment)                        //TODO implement it
class UserCommentConstraintsSpec extends Specification {

    def setup() {

    }

    @Unroll("Field '#field' of class UserComment should have constraint '#constraint' violated by value '#violatingValue'")
    def "UserComment should have well defined constraints:" () {

        when:
        def userComment = new UserComment("$field": violatingValue)


        then:
        userComment.validate() == false
        userComment.errors[field] == constraint
        true

        where:
        field           | constraint    | violatingValue
        'content'       | 'blank'       | ""
        'content'       | 'nullable'    | null
        'author'        | 'blank'       | ""
        'author'        | 'nullable'    | null


    }
}

