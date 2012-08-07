package codereview

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import grails.plugins.springsecurity.SpringSecurityService

@TestFor(User)
class UserConstraintsSpec extends Specification {

    static String existingUserEmail = "already.existing@user.com"
    static String existingUserPassword = "dupa.8"

    def setup() {
        injectSecurityService(new User(existingUserEmail, existingUserPassword)).save()
    }

    @Unroll("Field '#field' of class User should have validation error '#error' caused by value '#violatingValue'")
    def "User should have well defined constraints:"() {

        when:
        def user = injectSecurityService(new User("$field": violatingValue))

        then:
        user.validate() == false
        user.errors.getFieldError(field).code == error

        where:
        field      | error           | violatingValue
        'username' | 'blank'         | ""
        'username' | 'email.invalid' | "obviusly @ not . an . email"
        'username' | 'unique'        | existingUserEmail
        'password' | 'blank'         | ""
        'email'    | 'nullable'      | null
        'password' | 'nullable'      | null
    }

    User injectSecurityService(User user) {
        user.springSecurityService = Mock(SpringSecurityService)
        user
    }
}
