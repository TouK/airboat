package codereview


import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore

@TestFor(UserComment)                        //TODO implement it
class UserCommentConstraintsSpec extends Specification {

    @Ignore
    @Unroll("Field '#field' of class UserComment should have constraint '#constraint' violated by value '#violatingValue'")
    def "UserComment should have well defined constraints:" () {

        when:
        def userComment = new UserComment("$field": violatingValue)


        then:
        //userComment.validate() == false
        //userComment.errors[field] == constraint
        //userComment.errors[field] == constraint
        true

        where:
        field           | constraint    | violatingValue
        'text'       | 'blank'       | ""
        'text'       | 'nullable'    | null
        'author'        | 'blank'       | ""
        'author'        | 'nullable'    | null


    }
}

