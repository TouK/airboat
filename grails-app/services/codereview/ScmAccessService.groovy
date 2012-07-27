package codereview

import org.apache.maven.scm.ChangeSet

/**
 * Deleguje operacje na projekcie w SCM do odpowiedniej implementacji w zależności od rodzaju repozutorium kodu.
 * Teraz działa tylko dla GIT.
 */
class ScmAccessService {

    GitRepositoryService gitRepositoryService

    void checkoutProject(String scmUrl) {
        gitRepositoryService.checkoutProject(scmUrl)
    }                                                  //TODO remove "with files" from methods names

    void updateProject(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
    }

    void fetchAllChangesetsWithFilesAndSave(String scmUrl) {
        fetchAllChangesetsWithFiles(scmUrl).each {
            if(it.validate()) {
                it.save(failOnError: true)
            }
        }
    }

    Changeset[] fetchAllChangesetsWithFiles(String gitScmUrl){
        List<org.apache.maven.scm.ChangeSet> scmChanges = gitRepositoryService.getAllChangeSets(gitScmUrl)
        if (scmChanges != null) {
            createChangesetsWithFiles(scmChanges)
        } else {
            return []
        }
    }


    def createChangesetsWithFiles(List<org.apache.maven.scm.ChangeSet> scmChanges) {

        scmChanges.collect { ChangeSet it ->

            def files = it.getFiles().collect { file ->
                new ProjectFile(file.getName(), gitRepositoryService.returnFileContent())
            }

            Changeset changeset = new Changeset(it.revision, it.author, it.comment, it.date)
            files.each {
                changeset.addToProjectFiles(it)
            }

            return changeset

        }.sort { it.date.time }
    }


}
