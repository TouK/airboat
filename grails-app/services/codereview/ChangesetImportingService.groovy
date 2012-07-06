package codereview

class ChangesetImportingService {

    GitRepositoryService gitRepositoryService

    void importFrom(String scmUrl) {
        gitRepositoryService.fetchChangelog(scmUrl).each { it.save() }
    }
}
