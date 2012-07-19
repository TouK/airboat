package codereview

class ChangelogAccessService {

    GitRepositoryService gitRepositoryService

    void fetchChangelogAndSave(String scmUrl) {

        //TODO save only new changes
        if(gitRepositoryService.fetchNewChangelog(scmUrl) != null) {
            gitRepositoryService.fetchNewChangelog(scmUrl).each {
                if(it.validate()) {
                    it.save()
                }
            }
        }
    }
}
