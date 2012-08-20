import codereview.Changeset
import codereview.Constants
import codereview.Project
import grails.converters.JSON

//import static codereview.ScmAccessService.getEmail

class BootStrap {

    //FIXME add a bootstrap test, errors here are too frequent...
    def init = { servletContext ->

        environments {
            production {
                createAndSaveConfiguredProjects()
            }
            development {
                createAndSaveConfiguredProjects()
            }
        }
    }

    private void createAndSaveConfiguredProjects() {
        new Project('codereview', Constants.PROJECT_CODEREVIEW_REPOSITORY_URL).save(flush: true)
        new Project('cyclone', Constants.PROJECT_CYCLONE_REPOSITORY_URL).save(flush: true)
    }

    def destroy = {
    }
}
