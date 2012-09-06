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
        importChangesets(projectScmUrl, gitRepositoryService.getAllChangesets(projectScmUrl))
    }

    void importNewChangesets(String projectScmUrl, String hashOfLastChangeset) {
        importChangesets(projectScmUrl, gitRepositoryService.getNewChangesets(projectScmUrl, hashOfLastChangeset))
    }

    private void importChangesets(String projectScmUrl, List<GitChangeset> gitChangesets) {
        def project = Project.findByUrl(projectScmUrl)
        gitChangesets.each { importChangeset(it, project) }
        project.save()
    }

    //FIXME boost performance with entity chaches
    private void importChangeset(GitChangeset gitChangeset, Project project) {
        Commiter commiter = Commiter.findOrCreateWhere(cvsCommiterId: gitChangeset.gitCommitterId)

        //TODO make all domain classes' constructors take as parameters all instances the class belongsTo
        Changeset changeset = new Changeset(gitChangeset.rev, gitChangeset.fullMessage, gitChangeset.date)
        commiter.addToChangesets(changeset)
        project.addToChangesets(changeset)

        commiter.save() //FIXME ask someone why this line is necessary here

        gitChangeset.files.each { GitChangedFile file ->
            //FIXME verify why no constraints are violated here (learning test?)
            ProjectFile projectFile = ProjectFile.findOrSaveWhere(name: file.name, project: project)
            new ProjectFileInChangeset(changeset, projectFile, convertChangeType(file.changeType))
        }

        changeset.save()
//        project.save() //FIXME check that files are associated with changesets

        def email = commiter.cvsCommiterId
        def user = User.findByEmail(email)
        user?.addToCommitters(commiter)        //FIXME what if commmitter exists in this user?
        user?.save()
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
