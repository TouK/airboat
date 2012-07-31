package codereview

import testFixture.Constants

class ProjectUpdateJob {

    ScmAccessService scmAccessService

    private static final long REPEAT_INTERVAL_MILLISECONDS = 60 * 1000L

    static triggers = {
      simple repeatInterval: REPEAT_INTERVAL_MILLISECONDS
    }

    def execute() {
        update()
    }

    def update() {
        log.info('Starting project update')
        scmAccessService.updateProject(Constants.PROJECT_REPOSITORY_URL)
        scmAccessService.fetchAllChangesetsWithFilesAndSave(Constants.PROJECT_REPOSITORY_URL)
        log.info('Done project update')
    }
}
