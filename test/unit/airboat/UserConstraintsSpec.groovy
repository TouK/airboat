package airboat

import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(User)
@Build(User)
class UserConstraintsSpec extends Specification {

    static String existingUserEmail = 'already.existing@user.com'

    def setup() {
        User.build(username: existingUserEmail)
    }

    @Unroll("Field '#field' of class User should have validation error '#error' caused by value '#violatingValue'")
    def 'User should have well defined constraints:'() {

        when: //TODO make constraints tests use build-test-data's .build() method
        def user = new User("$field": violatingValue)

        then:
        user.validate() == false
        user.errors.getFieldError(field).code == error

        where:
        field      | error           | violatingValue
        'username' | 'blank'         | ''
        'email'    | 'blank'         | ''
        'username' | 'email.invalid' | 'obviusly @ not . an . email'
        'email'    | 'email.invalid' | 'obviusly @ not . an . email'
        'username' | 'unique'        | existingUserEmail
        'email'    | 'unique'        | existingUserEmail
        'password' | 'blank'         | ''
        'email'    | 'nullable'      | null
        'password' | 'nullable'      | null
    }

}
