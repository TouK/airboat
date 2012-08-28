package codereview

import org.eclipse.jgit.diff.DiffEntry

/**
 * Deleguje operacje na projekcie w SCM do odpowiedniej implementacji w zależności od rodzaju repozutorium kodu.
 * Teraz działa tylko dla GIT.
 */
class ScmAccessService {

    GitRepositoryService gitRepositoryService

    void updateOrCheckOutRepository(String scmUrl) {
        gitRepositoryService.updateOrCheckOutRepository(scmUrl)
    }

    void importAllChangesets(String projectScmUrl) {
        def changesets = convertToChangesets(gitRepositoryService.getAllChangesets(projectScmUrl))
        saveChangesets(projectScmUrl, changesets)
    }

    void importNewChangesets(String projectScmUrl, String hashOfLastChangeset) {
        def changesets = convertToChangesets(gitRepositoryService.getNewChangesets(projectScmUrl, hashOfLastChangeset))
        saveChangesets(projectScmUrl, changesets)
    }

    private void saveChangesets(String projectScmUrl, List<Changeset> changesets) {
        def project = Project.findByUrl(projectScmUrl)
        changesets.each { saveChangeset(it, project) }
        project.save(failOnError: true, flush: true)
    }

    private void saveChangeset(Changeset changesetToSave, Project project) {
        def commiter = Commiter.findOrCreateWhere(cvsCommiterId: changesetToSave.commiter.cvsCommiterId)
        def email = commiter.cvsCommiterId
        def user = email ? User.findByEmail(email) : null
        commiter.addToChangesets(changesetToSave)
        if (user != null) {
            user.addToCommitters(commiter)
        }
        project.addToChangesets(changesetToSave)
        changesetToSave.projectFiles.each { it.project = project }
        commiter.save(failOnError: true, flush: true)
        if (user != null) {
            user.save(failOnError: true, flush: true)
        }
    }

    private List<Changeset> convertToChangesets(gitChangesets) {
        if (gitChangesets == null) {
            return []
        } else {
            gitChangesets.collect { buildChangeset(it) }
        }
    }

    private Changeset buildChangeset(GitChangeset gitChangeset) {
        Commiter commiter = new Commiter(gitChangeset.authorEmail)
        Changeset changeset = new Changeset(gitChangeset.rev, gitChangeset.fullMessage, gitChangeset.date)
        commiter.addToChangesets(changeset)
        if (gitChangeset?.files != null) {
            gitChangeset.files.each { GitChangedFile file ->
                changeset.addToProjectFiles(new ProjectFile(file.name, convertChangeType(file.changeType)))
            }
        }
        return changeset
    }

    ChangeType convertChangeType(DiffEntry.ChangeType changeType) {
        ChangeType.valueOf(ChangeType, changeType.toString())
    }

    String getFileContent(Changeset changeset, ProjectFile projectFile) {
        return gitRepositoryService.getFileContentFromChangeset(
                changeset.project.url,
                changeset.identifier,
                projectFile.name

        )
    }
}
