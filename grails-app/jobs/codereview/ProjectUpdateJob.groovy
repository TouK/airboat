package codereview

class ProjectUpdateJob {

    ScmAccessService scmAccessService

    static triggers = {
      simple repeatInterval: 30000l // execute job once in 10 seconds
    }

    def execute() {
        update()
    }

    def update() {
        scmAccessService.updateProject("git@git.touk.pl:touk/codereview.git")            //TODO: change it to variable from class Fixture
        scmAccessService.fetchAllChangesetsWithFilesAndSave("git@git.touk.pl:touk/codereview.git")   //TODO: change it to variable from class Fixture
    }
}
