package airboat

import org.springframework.transaction.support.DefaultTransactionStatus

class ProjectUpdateJob {

    def concurrent = false

    ScmAccessService scmAccessService

    private static final long REPEAT_INTERVAL_MILLISECONDS = 30 * 1000L

    static triggers = {
        simple repeatInterval: REPEAT_INTERVAL_MILLISECONDS
    }

    def execute() {
        time("execution of ProjectUpdateJob") {
            Project.all.each {Project project ->
                try {
                    initialImport(project)
                } catch (Exception e) {
                    log.error("Initial import of $project.url failed", e)
                    project.refresh()
                    project.state = Project.ProjectState.triedToBeInitiallyImported
                    project.save()
                }
            }

            Project.all.each { Project project ->
                try {
                    if (project.state != Project.ProjectState.notImported) {
                        def projects = Project.findAllByState(Project.ProjectState.notImported)
                        if (project.state != Project.ProjectState.fullyImported &&
                                Project.findAllByState(Project.ProjectState.notImported).size() == 0) {
                            fullImport(project)
                        }
                        update(project)
                    }
                } catch (Exception e) {
                    log.error("Import of next changesets for $project.url failed", e)
                }
            }
        }
    }

    def initialImport(Project project) {
        Project.withTransaction({DefaultTransactionStatus status ->
            time("initial import of $project.url") {
                scmAccessService.updateOrCheckOutRepository(project.url)
                if (project.state == Project.ProjectState.notImported) {
                    scmAccessService.importLastChangesets(project.url)
                    project.state = Project.ProjectState.initiallyImported
                    project.save()
                }
            }
        })
    }

    def fullImport(Project project) {
        Project.withTransaction({DefaultTransactionStatus ignoredStatus ->
            time("full import of $project.url") {
                scmAccessService.updateOrCheckOutRepository(project.url)
                Changeset oldestChangeset = Changeset.findByProject(project, [sort: 'date', order: 'asc'])
                if (oldestChangeset != null) {
                    scmAccessService.importRestChangesets(project.url, oldestChangeset.identifier)
                } else {
                    scmAccessService.importLastChangesets(project.url)
                }
                project.state = Project.ProjectState.fullyImported
                project.save()
            }
        })
    }

    def update(Project project) {
        String projectRepositoryUrl = project.url
        Project.withTransaction({ DefaultTransactionStatus ignoredStatus ->
            time("update of project $project.url") {
                scmAccessService.updateOrCheckOutRepository(projectRepositoryUrl)
                Changeset lastChangeset = Changeset.findByProject(project, [sort: 'date', order: 'desc'])
                if (lastChangeset != null) {
                    scmAccessService.importChangesetsSince(projectRepositoryUrl, lastChangeset.identifier)
                } else {
                    scmAccessService.importChangesetsSinceBegining(projectRepositoryUrl)
                }
            }
        })
    }

    def time(String actionName, Closure action) {
        long startTime = System.nanoTime()
        log.info("Starting $actionName")
        try {
            action()
            log.info("Finished $actionName. It completed successfully after ${durationSince(startTime)}")
        } catch (Exception e) {
            log.warn("Finished $actionName. It FAILED after ${durationSince(startTime)}")
            throw e
        }
    }

    private String durationSince(long startTimeNanos) {
        "${(System.nanoTime() - startTimeNanos) / 1000 / 1000 / 1000} seconds."
    }
}
