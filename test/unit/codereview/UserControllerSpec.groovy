package codereview

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import grails.plugins.springsecurity.SpringSecurityService
import spock.lang.Specification

@TestFor(UserController)
@Mock([User, Commiter])
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
}
