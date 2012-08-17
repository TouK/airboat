package codereview

import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.api.Git

class GitRepositoryService {

    def diffAccessService
    def infrastructureService

    def createRepository(String scmUrl) {
        String projectName = getProjectNameFromScmUrl(scmUrl)
        def PATH = infrastructureService.getFullPathForProjectWorkingDirectory(projectName)
        File gitDir = new File(PATH + "/.git")
        if (!gitDir.exists()) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(PATH))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        Git git = new Git(repository)
        git.cloneRepository()
        .setBare(false)
        .setCloneAllBranches(true)
        .setDirectory(new File(PATH)).setURI(scmUrl)
        .call()
        }
    }

    def updateRepository(String scmUrl) {
        String projectName = getProjectNameFromScmUrl(scmUrl)

        def PATH = infrastructureService.getFullPathForProjectWorkingDirectory(projectName) + "/.git"
        File gitDir = new File(PATH)
        if(!gitDir.exists()) {
            createRepository(scmUrl)
        }
        Repository repository = diffAccessService.getRepositoryFromWorkingDirectory(PATH)

        Git git = new Git(repository)
        assert(!repository.isBare())
        def pullResult = git.pull().call()
        [success: pullResult.successful, from: pullResult.fetchedFrom, fetchProperties: pullResult.fetchResult.properties,
                mergeResult: pullResult.mergeResult.toString() ]
    }

    def getAllChangesets(String scmUrl) {
        Git git = prepareGit(scmUrl)
        def logOutput = git.log().call()
        prepareGitChangesets(logOutput, git.repository.directory.absolutePath)

    }

    String getProjectNameFromScmUrl(String scmUrl) {
        scmUrl.split("/").last()[0..-5]
    }

    def prepareGitChangesets(logOutput, String PATH) {
        def logIterator = logOutput.iterator()
        def changesets = []
        while(logIterator.hasNext())  {
            def commit = logIterator.next()
            def commitHash = commit.toString().split(" ")[1]
            GitChangeset gitChangeset = new GitChangeset( commit.fullMessage, commit.authorIdent.emailAddress, commitHash, new Date(commit.getCommitTime() *1000L ) )
            gitChangeset.files = diffAccessService.getChangedFilesToCommit(PATH, commitHash)
            changesets.add(gitChangeset)
        }
        return changesets
    }

    def getNewChangesets(String scmUrl, String lastChangesetHash) {
        Git git = prepareGit(scmUrl)
        def lastSavedChangesetId = git.repository.resolve(lastChangesetHash)
        def logOutput = git.log().not(lastSavedChangesetId).call()
        prepareGitChangesets(logOutput, git.repository.directory.absolutePath)
    }

    def prepareGit(String scmUrl) {
        def projectName = getProjectNameFromScmUrl(scmUrl)
        def PATH = infrastructureService.getFullPathForProjectWorkingDirectory(projectName)  + "/.git"
        Repository repository = diffAccessService.getRepositoryFromWorkingDirectory(PATH)
        new Git(repository)
    }

}
