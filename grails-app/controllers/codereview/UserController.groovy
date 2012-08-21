package codereview

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class UserController {

    static allowedMethods = [save: 'POST']

    def springSecurityService

    def create() {
        [command: new CreateUserCommand()]
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
