package codereview

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class UserController {

    static allowedMethods = [save: 'POST']

    def springSecurityService
    def authenticationManager

    def create() {
    }


    @Secured('hasRole("ROLE_ADMIN")')
    def admin() {

    }

    def save(CreateUserCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(command.errors as JSON)
        } else {
            validateDbDependentConstraintsAndSaveUser(command)
        }
    }

    @Secured('isAuthenticated()')
    def changePassword() {
    }

    def saveNewPassword(ChangePasswordCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(command.errors as JSON)
        } else {
            validateOldPasswordAndSetNewPassword(command)
        }
    }

    private void validateOldPasswordAndSetNewPassword(ChangePasswordCommand command) {
        if (isPasswordValid(command.oldPassword)) {
            authenticatedUser.password = command.newPassword
            authenticatedUser.save()
            render([success: true, message: message(code:'passwordChanged')] as JSON)
        } else {
            command.errors['oldPassword'] = 'invalidPassword'
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

    @Secured('isAuthenticated()')
    def options() {

    }

    def fetchSkinOptions(String id) {
        def skin = User.findByEmail(id).skin

        render ([skin: skin] as JSON )

    }

    def setSkinOptions(String username, String skin) {
        User user = User.findByEmail(username)
        user.skin =  skin
        user.save()
        render (user as JSON)
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
        user.save(failOnError: true)
    }


}
