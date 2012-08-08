package codereview

import grails.plugin.spock.IntegrationSpec

class UserControllerIntegrationSpec extends IntegrationSpec {

    def springSecurityService

    def controller = new UserController()

    def "should save and enable user"() {
        given:
        def username = "agj@touk.pl"
        String password = "dupa.8"
        controller.springSecurityService = springSecurityService //TODO examine if spring can do it for us here


        when:
        controller.save(new CreateUserCommand(
                email: username,
                password: password,
                password2: password
        ))

        then:
        User.count() == 1
        User.findByEmail(username).enabled == true
    }

    def "should associate user with committer on user creation"() {
        given:
        def username = "agj@touk.pl"
        String password = "dupa.8"
        new Commiter("${username}").save()
        new Commiter("Artur Gajowy <${username}>").save()
        new Commiter("Gajowy <${username}> some cvs related garbage suffix").save()
        new Commiter("Will not be included <kpt@touk.pl>").save()

        when:
        controller.save(new CreateUserCommand(
                email: username,
                password: password,
                password2: password
        ))

        then:
        User.count == 1
        User.findByEmail(username).committers.size() == 3
    }

}
