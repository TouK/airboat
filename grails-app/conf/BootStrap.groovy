import codereview.Changeset
import codereview.Constants
import codereview.Project
import grails.converters.JSON

import static codereview.Constants.*
import codereview.ProjectUpdateJob
import codereview.User
import codereview.Role
import codereview.UserRole

class BootStrap {

    def init = { servletContext ->

        environments {
            production {
                bootstrapNonTestEnvironment(projectCodeReview())
            }
            development {
                bootstrapNonTestEnvironment(
                    projectCodeReview(),
                    new Project('drip', 'https://github.com/flatland/drip.git'),
                    new Project('visibility.js', 'https://github.com/ai/visibility.js.git'),
                )
            }
        }
    }

    private Project projectCodeReview() {
        new Project('codereview', PROJECT_CODEREVIEW_REPOSITORY_URL)
    }

    private void bootstrapNonTestEnvironment(Project... projects) {
        projects.each { it.save(flush: true) }
        ProjectUpdateJob.triggerNow()
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
