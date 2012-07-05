package codereview

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository
import org.apache.maven.scm.repository.ScmRepository
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider

import org.apache.maven.scm.ChangeSet

class GitRepository {
    Changeset[] fetchChangelog(String gitScmUrl) {
        //FIXME this assumes that only this project's gitScmUrl will be ever passed to this method
        ScmFileSet allFilesInProject = new ScmFileSet(new File("."), "*.*")
        def gitRepository = new ScmRepository("git", new GitScmProviderRepository(gitScmUrl))
        def changeLogScmResult = new GitExeScmProvider().changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, "master")
        List<ChangeSet> changeLog = changeLogScmResult.getChangeLog().getChangeSets()
        changeLog
                .collect { new Changeset(it.revision, it.author, it.date) }
                .sort { it.date.time } //TODO it seems that somehow sort order is build-depenent (IDEA vs Grails)
    }
}
