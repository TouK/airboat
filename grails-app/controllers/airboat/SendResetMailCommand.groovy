package airboat

import grails.validation.Validateable

@Validateable
class SendResetMailCommand {

    String email

    static constraints = {
        importFrom User, include: ['email']
    }
}
