package airboat

import grails.validation.Validateable

@Validateable
class ChangePasswordCommand {

    String oldPassword
    String newPassword
    String newPasswordRepeat

    static constraints = {
        oldPassword blank:  false
        newPassword blank:  false
        newPasswordRepeat blank: false, validator: passwordsMatch
    }

    static def passwordsMatch = { newPasswordRepeat, that ->
        that.newPassword == newPasswordRepeat ? true : ['passwordsMatch']
    }
}
