package codereview

class ChangesetImportingService {

    GitRepositoryService gitRepositoryService



    //checkout
    //utworz katalog
    //zrob checkout

    //update
    //w odpowiednim katalogu zaktualizuj projekt

    void importFrom(String scmUrl) {
        gitRepositoryService.fetchChangelog(scmUrl).each { it.save() }
    }
}
