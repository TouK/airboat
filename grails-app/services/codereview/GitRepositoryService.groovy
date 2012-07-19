package codereview

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository
import org.apache.maven.scm.repository.ScmRepository
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.ChangeSet

class GitRepositoryService {

    def infrastructureService

    void checkoutProject(String gitScmUrl) {
        ScmFileSet allFilesInProject = prepareScmFileset(gitScmUrl)
        ScmRepository gitRepository = createScmRepositoryObject(gitScmUrl)

        new GitExeScmProvider().checkOut(gitRepository, allFilesInProject)
    }

    void updateProject(String gitScmUrl) {
        ScmFileSet allFilesInProject = prepareScmFileset(gitScmUrl)
        ScmRepository gitRepository = createScmRepositoryObject(gitScmUrl)

        //TODO test for it
        if (validateScmFileset(allFilesInProject)) {
            def scmProvider = new GitExeScmProvider()
            scmProvider.addListener(new Log4jScmLogger())
            scmProvider.update(gitRepository, allFilesInProject)
        } else {
            log.warn("Project direcotry does not exist yet. Please checkout project first.")
        }
    }

    def validateScmFileset(ScmFileSet scmFileSet) {
        return scmFileSet.basedir.exists()
    }

    Changeset[] fetchFullChangelog(String gitScmUrl) {
        List<ChangeSet> changes =   getGitChangeSets( gitScmUrl)

        if (changes != null)
        returnChangesetsWithAddedFiles(changes)
        else
        return null
    }

    Changeset[] fetchNewChangelog(String gitScmUrl){
        List<ChangeSet> changes = getGitChangeSets(gitScmUrl)
        if(changes != null)  {
            returnChangesetsWithAddedFiles(changes)
        }
        else return null
    }

    List<ChangeSet> getGitChangeSets(String gitScmUrl)   {
        ScmFileSet allFilesInProject = prepareScmFileset(gitScmUrl)
        ScmRepository gitRepository = createScmRepositoryObject(gitScmUrl)

        def scmProvider = new GitExeScmProvider()
        scmProvider.addListener(new Log4jScmLogger())
        def changeLogScmResult = scmProvider.changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, "master")

        List<ChangeSet> changes = changeLogScmResult.getChangeLog()?.getChangeSets()
    }


    def returnChangesetsWithAddedFiles(List<ChangeSet> changes){

        changes
                .collect {
                            if(it!=null){
                                def files = it.getFiles().collect { file ->
                                    new ProjectFile(name: file.getName())

                                }
                                Changeset changeset = new Changeset(it.revision, it.author, it.comment, it.date)
                                files.each {
                                    changeset.addToProjectFiles(it)
                                }
                                return changeset
                            }
                 }
                .sort { it.date.time } //TODO it seems that somehow sort order is build-depenent (IDEA vs Grails) - find cause
    }

    def getFileNamesFromChangeSetsList(List<ChangeSet> changes)    {

          changes
                  .collect {it.getFiles()}
    }
    def getFileNamesFromGitChangeSet(ChangeSet change)      {
        change.getFiles()
    }
    private ScmRepository createScmRepositoryObject(String gitScmUrl) {
        new ScmRepository("git", new GitScmProviderRepository(gitScmUrl))
    }

    private ScmFileSet prepareScmFileset(String gitScmUrl) {
        new ScmFileSet(infrastructureService.getProjectWorkingDirectory(gitScmUrl), "*.*")
    }


}
