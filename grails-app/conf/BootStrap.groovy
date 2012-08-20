import codereview.Changeset
import codereview.Constants
import codereview.Project
import grails.converters.JSON

import static codereview.Constants.*
import codereview.ProjectUpdateJob

class BootStrap {

    //FIXME add a bootstrap test, errors here are too frequent...
    def init = { servletContext ->

        environments {
            production {
                createAndSaveConfiguredProjects(['cyclone', 'franek-kimono', 'cyclos', 'zephyr', 'bb-mobile', 'breeze',
                        'cyclos-adapter', 'mobilizer', 'stratus', 'cyclone-sms'])
                ProjectUpdateJob.triggerNow()
            }
            development {
                createAndSaveConfiguredProjects(['cyclone', 'cyclos', 'franek-kimono'])
                ProjectUpdateJob.triggerNow()
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

    def destroy = {
    }
}
