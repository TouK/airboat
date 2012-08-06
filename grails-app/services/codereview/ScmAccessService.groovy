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
    }

    void updateProject(String scmUrl) {
        gitRepositoryService.updateProject(scmUrl)
    }

    void fetchAllChangesetsAndSave(String scmUrl) {
        def project = Project.findByUrl(scmUrl)
        fetchAllChangesets(scmUrl).each {
            project.addToChangesets(it)
        }
        project.save(failOnError: true)
    }

    Changeset[] fetchAllChangesets(String gitScmUrl){
        List<org.apache.maven.scm.ChangeSet> scmChanges = gitRepositoryService.getAllChangeSets(gitScmUrl)
        if (scmChanges != null) {
            createChangesets(scmChanges)
        } else {
            return []
        }
    }


    def createChangesets(List<ChangeSet> scmChanges) {

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
