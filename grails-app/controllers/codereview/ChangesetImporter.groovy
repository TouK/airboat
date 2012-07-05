package codereview

class ChangesetImporter {

    private GitRepository gitRepository

    ChangesetImporter(GitRepository gitRepository) {
        this.gitRepository = gitRepository
    }

    void importFrom(String scmUrl) {
        gitRepository.fetchChangelog(scmUrl).each { it.save() }
    }
}
