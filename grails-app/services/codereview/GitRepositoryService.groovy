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
     * @return
     */
    File resolveProjectWorkingDirectory(String scmUrl) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        log.info("basedir is: " + baseDir)

        File dir = new File(baseDir, scmUrl.hashCode().toString());
        log.info("project working directory is: " + dir)

        if (dir.exists()) {
            return dir;
        }

        if (dir.mkdir()) {
            return dir;
        }

        throw new IllegalStateException("Failed to create directory: " + dir);
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
