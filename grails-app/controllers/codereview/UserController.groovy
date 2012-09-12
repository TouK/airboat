package codereview

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.mail.MailSendException

class UserController {

    static allowedMethods = [save: 'POST']

    def springSecurityService
    def authenticationManager
    def mailService

    def create() {
    }

    def save(CreateUserCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(command.errors as JSON)
        } else {
            validateDbDependentConstraintsAndSaveUser(command)
        }
    }

    @Secured('hasRole("ROLE_ADMIN")')
    def admin() {
    }

    def forgottenPassword() {
    }

    def sendMailToResetPassword(SendResetMailCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(command.errors as JSON)
        } else {
            validateUserAndSendMail(command)
        }

    }

    def resetPassword() {
        def token = params.id
        def resetEntry = ResetPasswordEntry.findByToken(token)
        if (resetEntry == null) {
            redirect(action: 'resetPasswordFail')
        } else {
            [username: resetEntry.user.username, token: token]
        }
    }

    def resetPasswordFail() {

    }

    def saveNewPassword(NewPasswordCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(command.errors as JSON)
        } else {
            def resetEntry = ResetPasswordEntry.findByToken(command.token)
            if (resetEntry == null) {
                command.errors['token'] = 'invalidToken'
                render(command.errors as JSON)
            } else {
                setNewPassword(resetEntry.user, command.newPassword)
                resetEntry.delete()
                render([success: true, message: message(code: 'passwordChanged')] as JSON)
            }
        }
    }

    @Secured('isAuthenticated()')
    def options() {
    }

    def fetchSkinOptions(String id) {
        def skin = User.findByEmail(id).skin

        render([skin: skin] as JSON)

    }

    def setSkinOptions(String username, String skin) {
        User user = User.findByEmail(username)
        user.skin = skin
        user.save()
        render(user as JSON)
    }

    def saveChangedPassword(ChangePasswordCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(command.errors as JSON)
        } else {
            validateOldPasswordAndSetNewPassword(command)
        }
    }

    private validateUserAndSendMail(SendResetMailCommand command) {
        def user = User.findByEmail(command.email)
        if (user == null) {
            command.errors['email'] = 'userDoesNotExist'
            render(command.errors as JSON)
        }
        else {
            def resetEntry = ResetPasswordEntry.findByUser(user)
            if (resetEntry == null) {
                resetEntry = new ResetPasswordEntry(user: user)
            }
            resetEntry.token = generateToken()
            def link = createLink([controller: 'user', action: 'resetPassword', id: resetEntry.token, absolute: true])
            resetEntry.save()
            try {
            mailService.sendMail {
                to command.email
                subject message(code: 'resetEmailSubject')
                body message(code: 'resetEmailContent', args: [link])
            }
            } catch (MailSendException e) {
                log.warn('The email sending failed. Check mail server configuration.', e)
                command.errors['email'] = 'noMailServerConfigured'
                return render(command.errors as JSON)
            }
            render([success: true, message: message(code: 'mailSent')] as JSON)
        }
    }


    private String generateToken() {
        return org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(Constants.RESET_PASSWORD_TOKEN_LENGTH)
    }


    private void validateOldPasswordAndSetNewPassword(ChangePasswordCommand command) {
        if (isPasswordValid(command.oldPassword)) {
            setNewPassword(authenticatedUser, password)
            render([success: true, message: message(code: 'passwordChanged')] as JSON)
        } else {
            command.errors['oldPassword'] = 'invalidOldPassword'
            render(command.errors as JSON)
        }
    }

    private boolean isPasswordValid(password) {
        def isPasswordValid
        try {
            authenticationManager.authenticate new UsernamePasswordAuthenticationToken(authenticatedUser.username, password)
            isPasswordValid = true
        } catch (AuthenticationException e) {
            isPasswordValid = false;
        }
        isPasswordValid
    }

    private void setNewPassword(user, password) {
        user.password = password
        user.save()
    }


    private void validateDbDependentConstraintsAndSaveUser(CreateUserCommand command) {
        def user = new User(command.properties)
        if (user.validate()) {
            saveUser(user)
            springSecurityService.reauthenticate(user.username)
            redirect(controller: 'login', action: 'ajaxSuccess')
        } else if (user.errors.getFieldError('email').code == 'unique') {
            command.errors['email'] = 'userExists'
            render(command.errors as JSON)
        } else {
            throw new ThingThatShouldNotBeException("Unhandled validation errors in: ${user.errors.allErrors*.codes}")
        }
    }

    //FIXME move to service
    private void saveUser(User user) {
        user.enabled = true
        Commiter.findAllByCvsCommiterIdIlike("%${user.email}%").each {
            user.addToCommitters(it)
        }
        user.save()
    }


}
