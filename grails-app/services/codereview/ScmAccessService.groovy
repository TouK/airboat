package codereview

/**
 * Deleguje operacje na projekcie w SCM do odpowiedniej implementacji w zależności od rodzaju repozutorium kodu.
 * Teraz działa tylko dla GIT.
 */
class ScmAccessService {

    GitRepositoryService gitRepositoryService

    void checkoutProject(String scmUrl) {
        gitRepositoryService.createRepository(scmUrl)
    }

    void updateProject(String scmUrl) {
        gitRepositoryService.updateRepository(scmUrl)
    }

    def importAllChangesets(String projectScmUrl) {
        def project = Project.findByUrl(projectScmUrl)
        saveChangesets(fetchAllChangesets(projectScmUrl), project)
        project.save(failOnError: true, flush: true)
    }

    def importNewChangesets(String projectScmUrl,String hashOfLastChangeset) {
        def project = Project.findByUrl(projectScmUrl)
        saveChangesets(fetchNewChangesetsSince(projectScmUrl, hashOfLastChangeset), project)
        project.save(failOnError: true, flush: true)
    }

    def saveChangesets(changesets, project) {
        changesets.each { Changeset changesetToSave ->
            def commiter = Commiter.findOrCreateWhere(cvsCommiterId: changesetToSave.commiter.cvsCommiterId)
            def email = commiter.cvsCommiterId
            def user = email ? User.findByEmail(email) : null
            commiter.addToChangesets(changesetToSave)
            if (user != null) {
                user.addToCommitters(commiter)
            }
            project.addToChangesets(changesetToSave)
            commiter.save(failOnError: true, flush: true)
            if (user != null) {
                user.save(failOnError: true, flush: true)
            }
        }
    }

    def fetchAllChangesets(String projectScmUrl) {
        convertToChangesets(gitRepositoryService.getAllChangesets(projectScmUrl))
    }

    def fetchNewChangesetsSince(String projectScmUrl, String lastChangesetHash) {
        convertToChangesets(gitRepositoryService.getNewChangesets(projectScmUrl, lastChangesetHash))
    }

    def convertToChangesets(gitChangesets) {
        if (gitChangesets == null) {
            return []
        } else {
            gitChangesets.collect { buildChangeset(it) }
        }
    }

    Changeset buildChangeset(GitChangeset gitChangeset) {
        Commiter commiter = new Commiter(gitChangeset.authorEmail)
        Changeset changeset = new Changeset(gitChangeset.rev, gitChangeset.fullMessage, gitChangeset.date)
        commiter.addToChangesets(changeset)
        if (gitChangeset?.files != null) {
            gitChangeset.files.each { file ->
                changeset.addToProjectFiles(new ProjectFile(file.name))
            }
        }
        return changeset
    }
}
