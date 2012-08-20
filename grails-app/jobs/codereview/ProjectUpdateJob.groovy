package codereview

class ProjectUpdateJob {

    def concurrent = false

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

    //FIXME this import is incremental only thanks to exceptions stopping it in the middle. Ugly.
    //Probably it's incremental nature should be reflected by signatures of methods used here.
    def update(String projectRepositoryUrl) {
        Project.withTransaction({ //TODO this withTransaction call is redundant according to quartz-plugin docs. Verify.
            log.info("Starting project update for project ${projectRepositoryUrl}")
            scmAccessService.updateProject(projectRepositoryUrl)
            scmAccessService.importAllChangesets(projectRepositoryUrl)
            log.info("Done project update for project ${projectRepositoryUrl}")
        })
    }
}
