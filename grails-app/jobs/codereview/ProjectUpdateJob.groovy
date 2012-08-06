package codereview

class ProjectUpdateJob {

    ScmAccessService scmAccessService

    private static final long REPEAT_INTERVAL_MILLISECONDS = 30 * 1000L

    static triggers = {
      simple repeatInterval: REPEAT_INTERVAL_MILLISECONDS
    }

    def execute() {
        Project.all.each {
            update(it.url)
        }
    }

    def update(String projectRepositoryUrl) {
        log.info("Starting project update for project ${projectRepositoryUrl}")
        scmAccessService.updateProject(projectRepositoryUrl)
       scmAccessService.fetchAllChangesetsAndSave(projectRepositoryUrl)
        log.info("Done project update for project ${projectRepositoryUrl}")
    }
}
