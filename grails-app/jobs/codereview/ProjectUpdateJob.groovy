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
            update(it)
        }
    }

    def update(Project project) {
        String projectRepositoryUrl = project.url
        Project.withTransaction({
            log.info("Starting project update for project ${projectRepositoryUrl}")
            scmAccessService.updateProject(projectRepositoryUrl)
            if (project.hasChangesets()) {
                String lastChangesetHash = project.changesets.sort {it.date}.last().identifier
                scmAccessService.importNewChangesets(projectRepositoryUrl, lastChangesetHash)
            } else {
                scmAccessService.importAllChangesets(projectRepositoryUrl)
            }

            log.info("Done project update for project ${projectRepositoryUrl}")
        })
    }
}
