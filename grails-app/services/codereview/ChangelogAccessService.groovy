package codereview

class ChangelogAccessService {

    GitRepositoryService gitRepositoryService

    void fetchChangelogAndSave(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
        //TODO save only new changes
        gitRepositoryService.fetchFullChangelog(scmUrl).each { it.save() }
    }
}
