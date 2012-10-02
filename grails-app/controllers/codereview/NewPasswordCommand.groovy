package airboat
import grails.validation.Validateable

@Validateable
class NewPasswordCommand {

    String newPassword
    String newPasswordRepeat
    String token

    static constraints = {
        newPassword blank:  false
        newPasswordRepeat blank: false, validator: passwordsMatch
        token blank:false
    }

    static def passwordsMatch = { newPasswordRepeat, that ->
        that.newPassword == newPasswordRepeat ? true : ['passwordsMatch']
    }
}
