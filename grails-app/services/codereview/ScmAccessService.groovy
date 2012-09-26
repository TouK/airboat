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

    void importChangesetsSinceBegining(String projectScmUrl, int maxChangesetsToImport = Integer.MAX_VALUE) {
        importChangesets(projectScmUrl, gitRepositoryService.getAllChangesets(projectScmUrl, maxChangesetsToImport))
    }

    void importChangesetsSince(String projectScmUrl, String hashOfLastChangeset, int maxChangesetsToImport = Integer.MAX_VALUE) {
        importChangesets(projectScmUrl, gitRepositoryService.getNewChangesets(projectScmUrl, hashOfLastChangeset, maxChangesetsToImport))
    }

    private void importChangesets(String projectScmUrl, List<GitChangeset> gitChangesets) {
        def project = Project.findByUrl(projectScmUrl)
        gitChangesets.each { importChangeset(it, project) }
        project.save()
    }

    //FIXME boost performance with entity caches
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
            def projectFileInItsPreviousChangeset = ProjectFileInChangeset
                    .findByProjectFile(projectFile, [sort: 'changeset.date', order: 'desc'])
            def projectFileInChangeset = new ProjectFileInChangeset(changeset, projectFile, convertChangeType(file.changeType))
            projectFileInItsPreviousChangeset?.commentThreadsPositions?.each {
                new ThreadPositionInFile(projectFileInChangeset, it.thread, it.lineNumber)
            }
        }

        changeset.save()

        def email = commiter.cvsCommiterId
        def user = User.findByEmail(email)
        user?.addToCommitters(commiter)
        commiter.save()
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
