package codereview

import org.apache.maven.scm.ChangeSet
import com.google.common.annotations.VisibleForTesting

/**
 * Deleguje operacje na projekcie w SCM do odpowiedniej implementacji w zależności od rodzaju repozutorium kodu.
 * Teraz działa tylko dla GIT.
 */
class ScmAccessService {

    GitRepositoryService gitRepositoryService

    void checkoutProject(String scmUrl) {
        gitRepositoryService.checkoutProject(scmUrl)
    }

    void updateProject(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
    }

    void importAllChangesets(String scmUrl) {
        fetchAllChangesets(scmUrl).each {
            saveChangeset(it) //TODO examine if "fetchAllChangesets(scmUrl).each(saveChangeset)" is somehow possible
        }
    }

    //TODO examine if global failOnError is possible
    //TODO examine if save does validate()
    @VisibleForTesting void saveChangeset(Changeset changesetToSave) {
        def commiter = changesetToSave.commiter
        def commiterFromDb = Commiter.find(commiter)
        if (commiterFromDb != null) {
            commiter = commiterFromDb
            commiter.addToChangesets(changesetToSave)
        }
        if (changesetToSave.validate()) {
            changesetToSave.save(failOnError: true)
            commiter.save(failOnError: true)
        }
    }

    Changeset[] fetchAllChangesets(String gitScmUrl){
        List<org.apache.maven.scm.ChangeSet> scmChanges = gitRepositoryService.getAllChangeSets(gitScmUrl)
        if (scmChanges != null) { //TODO move this if to convert function
            convertToChangesets(scmChanges)
        } else {
            return []
        }
    }

    Changeset[] convertToChangesets(List<org.apache.maven.scm.ChangeSet> scmChanges) {
        scmChanges.collect { convertToChangeset(it) }.sort { it.date.time }
    }

    @VisibleForTesting Changeset convertToChangeset(ChangeSet scmApiChangeSet) {
        Commiter commiter = new Commiter(scmApiChangeSet.getAuthor())
        Changeset changeset = new Changeset(scmApiChangeSet.revision, scmApiChangeSet.comment, scmApiChangeSet.date)
        commiter.addToChangesets(changeset)

        scmApiChangeSet.getFiles().each { file ->
            def projectFile = new ProjectFile(name: file.getName(), content: gitRepositoryService.returnFileContent())
            changeset.addToProjectFiles(projectFile)
        }

        return changeset
    }
}
