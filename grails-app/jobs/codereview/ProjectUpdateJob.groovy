package codereview

import testFixture.Constants
import org.springframework.transaction.annotation.Transactional

class ProjectUpdateJob {

    ScmAccessService scmAccessService

    private static final long REPEAT_INTERVAL_MILLISECONDS = 30 * 1000L

    static triggers = {
      simple repeatInterval: REPEAT_INTERVAL_MILLISECONDS
    }

    def execute() {
        update()
    }

    //FIXME this import is incremental only thanks to exceptions stopping it in the middle. Ugly.
    //Probably it's incremental nature should be reflected by signatures of methods used here.
    def update() {
        ProjectFile.withTransaction({
            log.info('Starting project update')
            scmAccessService.updateProject(Constants.PROJECT_REPOSITORY_URL)
            scmAccessService.importAllChangesets(Constants.PROJECT_REPOSITORY_URL)
            log.info('Done project update')
        })
    }
}
