import airboat.*

import static airboat.Constants.PROJECT_AIRBOAT_REPOSITORY_URL

class BootStrap {

    def init = { servletContext ->

        environments {
            production {
                bootstrapNonTestEnvironment(projectAirboat())
            }
            development {
                bootstrapNonTestEnvironment(
                        Project.findOrCreateWhere(name: 'drip', url: 'https://github.com/flatland/drip.git'),
                        Project.findOrCreateWhere(name: 'visibility.js', url: 'https://github.com/ai/visibility.js.git'),
                        projectAirboat()
                )
            }
        }
    }

    private Project projectAirboat() {
        Project.findOrCreateWhere(name: 'airboat', url:   PROJECT_AIRBOAT_REPOSITORY_URL)
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
        if(!User.findByEmail("admin@airboat.pl")) {
            admin = new User([username: "admin@airboat.pl", password: "admin", enabled: true])
            admin.save()
        }
        else {
            admin = User.findByEmail("admin@airboat.pl")
        }

        if(UserRole.findByRoleAndUser(administrator, admin) == null) {
            UserRole.create admin, administrator, true
        }

    }

    def destroy = {
    }
}
