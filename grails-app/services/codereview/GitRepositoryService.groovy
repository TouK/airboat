package codereview

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository
import org.apache.maven.scm.repository.ScmRepository
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmFile
import org.apache.maven.scm.command.diff.DiffScmResult

/**
 * Zabronione uzywanie klas domenowych w tej klasie.
 */
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

        if (validateScmFileset(allFilesInProject)) {
            def scmProvider = new GitExeScmProvider()
            scmProvider.addListener(new Log4jScmLogger())
            scmProvider.update(gitRepository, allFilesInProject)
        } else {
            log.warn("Project directory does not exist yet. Please, checkout project first.")
        }
    }

    def validateScmFileset(ScmFileSet scmFileSet) {
        return scmFileSet.basedir.exists()
    }

    Set<ChangeSet> getAllChangeSets(String gitScmUrl)   {
        ScmFileSet allFilesInProject = prepareScmFileset(gitScmUrl)
        ScmRepository gitRepository = createScmRepositoryObject(gitScmUrl)

        def scmProvider = new GitExeScmProvider()
        scmProvider.addListener(new Log4jScmLogger())
        def changeLogScmResult = scmProvider.changeLog(gitRepository, allFilesInProject, new Date(0), new Date(), 0, "master")

        changeLogScmResult.getChangeLog()?.getChangeSets() as Set<ChangeSet>
    }

    private ScmRepository createScmRepositoryObject(String gitScmUrl) {
        new ScmRepository("git", new GitScmProviderRepository(gitScmUrl))
    }

    private ScmFileSet prepareScmFileset(String gitScmUrl) {
        new ScmFileSet(infrastructureService.getProjectWorkingDirectory(gitScmUrl), "*.*")
    }
}

