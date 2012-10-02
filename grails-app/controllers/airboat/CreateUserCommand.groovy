package airboat

import grails.validation.Validateable

@Validateable
class CreateUserCommand {

    String email
    String password
    String password2

    static constraints = {
        importFrom User, include: ['email']
        password blank: false, validator: passwordsMatch
    }

    static def passwordsMatch = { password, that ->
        that.password2 == password ? true : ['passwordsMatch']
    }
}