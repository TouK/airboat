package codereview

import testFixture.Constants

class ProjectUpdateJob {

    ScmAccessService scmAccessService

    private static final long REPEAT_INTERVAL_MILLISECONDS = 30 * 1000L

    static triggers = {
      simple repeatInterval: REPEAT_INTERVAL_MILLISECONDS
    }

    def execute() {
        update()
    }

    def update() {
        scmAccessService.updateProject(Constants.PROJECT_REPOSITORY_URL)
        scmAccessService.fetchAllChangesetsWithFilesAndSave(Constants.PROJECT_REPOSITORY_URL)
    }
}
