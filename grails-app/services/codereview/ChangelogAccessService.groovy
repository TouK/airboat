package codereview

class ChangelogAccessService {

    GitRepositoryService gitRepositoryService

    void fetchChangelogAndSave(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
        //TODO save only new changes
        if(gitRepositoryService.fetchNewChangelog(scmUrl) != null) {
            gitRepositoryService.fetchNewChangelog(scmUrl).each { it.save() }
        }
    }
}
