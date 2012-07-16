package codereview

class ChangelogAccessService {

    GitRepositoryService gitRepositoryService

    void fetchChangelogAndSave(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
        //TODO save only new changes
        gitRepositoryService.fetchNewChangelog(scmUrl).each { it.save() }
    }
}
