import codereview.Changeset
import codereview.Constants
import codereview.Project
import grails.converters.JSON

import static codereview.Constants.*
import codereview.ProjectUpdateJob
import codereview.User
import codereview.Role
import codereview.UserRole
import codereview.ResetPasswordEntriesDeleteJob

class BootStrap {

    def init = { servletContext ->

        environments {
            production {
                bootstrapNonTestEnvironment(projectCodeReview())
            }
            development {
                bootstrapNonTestEnvironment(
                        Project.findOrCreateWhere(name: 'drip', url: 'https://github.com/flatland/drip.git'),
                        Project.findOrCreateWhere(name: 'visibility.js', url: 'https://github.com/ai/visibility.js.git'),
                        projectCodeReview()
                )
            }
        }
    }

    private Project projectCodeReview() {
        Project.findOrCreateWhere(name: 'codereview', url:   PROJECT_CODEREVIEW_REPOSITORY_URL)
    }

    private void bootstrapNonTestEnvironment(Project... projects) {
        projects.each { it.save(flush: true) }
        ProjectUpdateJob.triggerNow()
        ResetPasswordEntriesDeleteJob.triggerNow()
        createAdmin()
    }


    def createAdmin() {
        Role administrator
        if(!Role.findByAuthority("ROLE_ADMIN")){
            administrator = new Role()
            administrator.authority = "ROLE_ADMIN"
            administrator.save()
        }
        else {
            administrator =  Role.findByAuthority("ROLE_ADMIN")
        }

        User admin
        if(!User.findByEmail("admin@codereview.pl")) {
            admin = new User([username: "admin@codereview.pl", password: "admin", enabled: true])
            admin.save()
        }
        else {
            admin = User.findByEmail("admin@codereview.pl")
        }

        if(UserRole.findByRoleAndUser(administrator, admin) == null) {
            UserRole.create admin, administrator, true
        }

    }

    def destroy = {
    }
}
