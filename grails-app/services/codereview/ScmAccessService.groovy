package codereview

import com.google.common.annotations.VisibleForTesting
import org.apache.maven.scm.ChangeSet

/**
 * Deleguje operacje na projekcie w SCM do odpowiedniej implementacji w zależności od rodzaju repozutorium kodu.
 * Teraz działa tylko dla GIT.
 */
class ScmAccessService {

    GitRepositoryService gitRepositoryService

    //FIXME this is Git-specific
    static String getEmail(String gitCommiterId) {
        if (gitCommiterId.contains("@")) {
            return gitCommiterId[gitCommiterId.indexOf("<") + 1..gitCommiterId.indexOf(">") - 1]
        } else {
            return null;
        }
    }

    void checkoutProject(String scmUrl) {
        gitRepositoryService.checkoutProject(scmUrl)
    }                                                  //TODO remove "with files" from methods names

    void updateProject(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
    }

    //TODO examine if global failOnError is possible
    //TODO examine if "fetchAllChangesets(scmUrl).each(saveChangeset)" is somehow possible
    void importAllChangesets(String projectScmUrl) {
        def project = Project.findByUrl(projectScmUrl)

        fetchAllChangesets(projectScmUrl).each { Changeset changesetToSave ->
            def commiter = Commiter.findOrCreateWhere(cvsCommiterId: changesetToSave.commiter.cvsCommiterId)
            def email = getEmail(commiter.cvsCommiterId)
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

        project.save(failOnError: true, flush: true)
    }

    Set<Changeset> fetchAllChangesets(String gitScmUrl) {
        Set<org.apache.maven.scm.ChangeSet> scmChanges = gitRepositoryService.getAllChangeSets(gitScmUrl)
        convertToChangesets(scmChanges)
    }

    Set<Changeset> convertToChangesets(Set<org.apache.maven.scm.ChangeSet> scmChanges) {
        if (scmChanges == null) {
            return []
        } else {
            scmChanges.collect { convertToChangeset(it) }
        }
    }

    @VisibleForTesting
    Changeset convertToChangeset(ChangeSet scmApiChangeSet) {
        Commiter commiter = new Commiter(scmApiChangeSet.getAuthor())
        Changeset changeset = new Changeset(scmApiChangeSet.revision, scmApiChangeSet.comment, scmApiChangeSet.date)
        commiter.addToChangesets(changeset)

        scmApiChangeSet.getFiles().each { file ->
            def projectFile = new ProjectFile(file.getName(), gitRepositoryService.returnFileContent())
            changeset.addToProjectFiles(projectFile)
        }

        return changeset
    }
}
