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

    //FIXME add a bootstrap test, errors here are too frequent...
    def init = { servletContext ->

        environments {
            production {
                createAndSaveConfiguredProjects(['cyclone', 'franek-kimono', 'cyclos', 'zephyr', 'bb-mobile', 'breeze',
                        'cyclos-adapter', 'mobilizer', 'stratus', 'cyclone-sms'])
                ProjectUpdateJob.triggerNow()
                createAdmin()
            }
            development {
                createAndSaveConfiguredProjects(['cyclone', 'cyclos', 'franek-kimono'])
                ProjectUpdateJob.triggerNow()
                createAdmin()
            }
        }
    }

    private void createAndSaveConfiguredProjects(ArrayList<String> qriosRepositoriesToImport) {
        def codereview = projectFromRepository(TOUK_GIT_REPOS_SERVER, 'touk', 'codereview')
        def projects = [codereview] + qriosRepositoriesToImport.collect { projectFromQriosRepository(it) }
        projects.each { Project it -> it.save(flush: true) }
    }

    private Project projectFromQriosRepository(String repositoryName) {
        projectFromRepository(TOUK_GIT_REPOS_SERVER, 'qrios', repositoryName)
    }

    private Project projectFromRepository(String repositoryServer, String projectName, String repositoryName) {
        new Project(repositoryName, "git://${repositoryServer}/${projectName}/${repositoryName}.git")
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
