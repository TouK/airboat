package codereview

class ChangelogAccessService {

    GitRepositoryService gitRepositoryService

    void fetchChangelogAndSave(String scmUrl) {
        if (gitRepositoryService.needsCheckOut()  )
            gitRepositoryService.checkoutProject(scmUrl)

        gitRepositoryService.updateProject(scmUrl)
        //TODO save only new changes
        if(gitRepositoryService.fetchNewChangelog() != null) {
            gitRepositoryService.fetchNewChangelog().each {
                if(it.validate())
                it.save() }
        }
    }
}
