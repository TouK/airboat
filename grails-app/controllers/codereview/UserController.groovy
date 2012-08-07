package codereview

class UserController {

    static allowedMethods = [save: 'POST']

    def springSecurityService

    def create() {
        [command: new CreateUserCommand()]
    }

    def save(CreateUserCommand command) {
        command.validate()
        if (command.hasErrors()) {
            render(view: 'create', model: [command: command])
        } else {
            validateDbDependentConstraintsAndSaveUser(command)
        }
    }

    private void validateDbDependentConstraintsAndSaveUser(CreateUserCommand command) {
        def user = new User(command.properties)
        if (user.validate()) {
            saveUser(user)
            springSecurityService.reauthenticate(user.username)
            redirect(controller: 'changeset', action: 'index')
        } else if (user.errors.getFieldError('email').code == 'unique') {
            command.errors['email'] = 'userExists'
            render(view: 'create', model: [command: command])
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
