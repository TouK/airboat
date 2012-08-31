package codereview

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class UserController {

    static allowedMethods = [save: 'POST']

    def springSecurityService

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
    def options() {

    }

    def fetchSkinOptions(String id) {
        def skin = User.findByEmail(id).skin

        render ([skin: skin] as JSON )

    }

    @Secured('isAuthenticated()')
    def dashboard() {

    }

    def changesets(String username) {
        User user = User.findByEmail(username)
        def changesets   = []
        user.committers.each {
            it.changesets.each {
                changesets.add(it)
            }
        }
        changesets = changesets.sort {it.date}
        render (changesets.subList(changesets.size()-6, changesets.size()-1) as JSON)
    }

    def projects(String username) {
        User user = User.findByEmail(username)
        def projects   = []
        user.committers.each {
            it.changesets.each {
                if(!projects.contains(it.project)) projects.add(it.project)
            }
        }
        render (projects as JSON)
    }

    def comments(String username) {
        User user = User.findByEmail(username)
        def comments   = []
        user.lineComments.each {
            comments.add(it)
        }
        render (comments as JSON)
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
