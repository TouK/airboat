package codereview

import grails.test.mixin.Mock

class UserController {

    static allowedMethods = [save: "POST"]

    def springSecurityService

    def create() {
        [command: new CreateUserCommand()]
    }

    def save(CreateUserCommand command) {
        if (command.hasErrors()) {
            render(view: 'create', model: [command: command])
        } else {
            def user = new User(command.properties)
            user.enabled = true
            if (user.validate()) {
                Commiter.findAllByCvsCommiterIdIlike("%${user.email}%").each {
                    user.addToCommitters(it)
                }
                user.save(failOnError: true)
                springSecurityService.reauthenticate(user.username)
                redirect(controller: "changeset", action: "index")
            } else {
                command.errors['email'] = 'userExists'
                render(view: 'create', model: [command: command])
            }
        }
    }
}
