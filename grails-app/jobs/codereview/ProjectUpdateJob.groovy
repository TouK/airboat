package codereview

class ProjectUpdateJob {

    ScmAccessService scmAccessService

    static triggers = {
      simple repeatInterval: 30000l // execute job once in 30 seconds?
        //TODO remove magical number from here and comment above, use constant variable instead
    }

    def execute() {
        update()
    }

    def update() {
        scmAccessService.updateProject("git@git.touk.pl:touk/codereview.git")            //TODO: change it to variable from class Fixture
        scmAccessService.fetchAllChangesetsWithFilesAndSave("git@git.touk.pl:touk/codereview.git")   //TODO: change it to variable from class Fixture
    }
}
