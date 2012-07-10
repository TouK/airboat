package codereview

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository
import org.apache.maven.scm.repository.ScmRepository
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.ChangeSet

class GitRepositoryService {

    /**
     * TODO naive name, refactor: single responsibility (i.e. extract creation of the name)
     * @param scmUrl
     * @return    l
     */
    boolean isValidDirectoryPath(String dirPath) { //TODO implement! and finish test!
        return true
    }

    File getBaseDir() {
        def result = System.getProperty("codereview.workingDirectory")
        File baseDir
        if(isValidDirectoryPath(result))     {

            if (result != null)     {
                baseDir = new File(result);
            }
            else {
                baseDir = new File(System.getProperty("java.io.tmpdir"));
            }
            return baseDir

        }
        else {
            throw new IllegalStateException("Cannot get base directory: " + baseDir)
        }

    }

    File createWorkingDirectory(File baseDir, String scmUrl){
        if(!baseDir.exists()) {
            baseDir.mkdir()
        }
        File dir = new File(baseDir, "/" + scmUrl.hashCode().toString())    // log.info("basedir is: " + baseDir)
        if (dir.exists()) {                                                 // log.info("project working directory is: " +dir.toString())
            return dir;
        }
        if (dir.mkdir()) {
            return dir;
        }

        throw new IllegalStateException("Failed to create directory: " + dir);
    }

    File resolveProjectWorkingDirectory(String scmUrl) {
        File baseDir = getBaseDir()
        createWorkingDirectory(baseDir, scmUrl)
    }

    Changeset[] fetchChangelog(String gitScmUrl) {
        //FIXME this assumes that only this project's gitScmUrl will be ever passed to this method
        ScmFileSet allFilesInProject = new ScmFileSet(resolveProjectWorkingDirectory(gitScmUrl), "*.*")
        def gitRepository = new ScmRepository("git", new GitScmProviderRepository(gitScmUrl))
        new GitExeScmProvider().checkOut(gitRepository, allFilesInProject) //TODO violates single responsibility principle
        def changeLogScmResult = new GitExeScmProvider().changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, "master")
        List<ChangeSet> changes = changeLogScmResult.getChangeLog().getChangeSets()

        changes
                .collect { new Changeset(it.revision, it.author, it.date) }
                .sort { it.date.time } //TODO it seems that somehow sort order is build-depenent (IDEA vs Grails) - find cause
    }
}
