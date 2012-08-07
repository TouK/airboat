package codereview

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import grails.plugins.springsecurity.SpringSecurityService
import spock.lang.Specification
import grails.buildtestdata.mixin.Build

@TestFor(UserController)
@Build([User, Commiter])
class UserControllerSpec extends Specification {

    def "should login newly created user"() {
        given:
        def username = "agj@touk.pl"
        controller.springSecurityService = Mock(SpringSecurityService)

        when:
        String password = "dupa.8"
        controller.save(new CreateUserCommand(
                email: username,
                password: password,
                password2: password
        ))

        then:
        1 * controller.springSecurityService.reauthenticate(username)
    }

    def "should reject 'jil@1' as a wrong email address"() {
        given:
        def username = "jil@1"
        controller.springSecurityService = Mock(SpringSecurityService)

        when:
        String password = "dupa.8"
        controller.save(new CreateUserCommand(
                email: username,
                password: password,
                password2: password
        ))

        then:
        view == '/user/create'
        model.command.errors.getFieldError('email').code == 'email.invalid'
    }

    def "should reject existing user's email as invalid"() {
        given:
        User user = User.build(springSecurityService: Mock(SpringSecurityService))

        when:
        String password = "dupa.8"
        controller.save(new CreateUserCommand(
                email: user.email,
                password: password,
                password2: password
        ))

        then:
        view == '/user/create'
        model.command.errors.getFieldError('email').code == 'userExists'
    }
}
