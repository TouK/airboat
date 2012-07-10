package codereview

class ChangesetImportingService {

    GitRepositoryService gitRepositoryService

    ChangesetImportingService() {
        this.gitRepositoryService = new GitRepositoryService()
    }
//checkout
    //utworz katalog
    //zrob checkout

    //update
    //w odpowiednim katalogu zaktualizuj projekt

    void importFrom(String scmUrl) {
        gitRepositoryService.fetchChangelog(scmUrl).each { it.save() }
    }
}
