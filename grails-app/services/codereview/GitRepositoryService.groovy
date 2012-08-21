package codereview

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.treewalk.TreeWalk

import org.eclipse.jgit.lib.ObjectLoader

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState

class GitRepositoryService {

    def diffAccessService
    def infrastructureService

    void createRepository(String scmUrl) {
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

    void updateRepository(String scmUrl) {
        String projectName = getProjectNameFromScmUrl(scmUrl)
        def PATH = infrastructureService.getFullPathForProjectWorkingDirectory(projectName) + "/.git"
        File gitDir = new File(PATH)
        if (!gitDir.exists()) {
            createRepository(scmUrl)
        }
        Repository repository = diffAccessService.getRepositoryFromWorkingDirectory(PATH)

        Git git = new Git(repository)
        assert (!repository.isBare())
        def pullResult = git.pull().call()
        checkState(pullResult.successful, "Failed to update ${scmUrl}")
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
        while (logIterator.hasNext()) {
            def commit = logIterator.next()
            def commitHash = commit.toString().split(" ")[1]
            GitChangeset gitChangeset = new GitChangeset(
                    commit.fullMessage,
                    commit.authorIdent.emailAddress,
                    commitHash,
                    new Date(commit.getCommitTime() * 1000L)
            )
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

    private Git prepareGit(String scmUrl) {
        def projectName = getProjectNameFromScmUrl(scmUrl)
        def PATH = infrastructureService.getFullPathForProjectWorkingDirectory(projectName) + "/.git"
        Repository repository = diffAccessService.getRepositoryFromWorkingDirectory(PATH)
        new Git(repository)
    }

    String getFileContentFromChangeset(String scmUrl, String revisionString, String fileName) {
        def git = prepareGit(scmUrl)
        def repository = git.repository
        ObjectId changesetId = repository.resolve(revisionString)
        checkArgument(changesetId != null, "No such revision in ${scmUrl}: ${revisionString}")
        RevCommit revCommit = new RevWalk(repository).parseCommit(changesetId)
        def treeWalk = TreeWalk.forPath(repository, fileName, revCommit.getTree())
        checkArgument(treeWalk != null, "No such file in ${scmUrl}/${revisionString}: ${fileName}")
        ObjectLoader objectLoader = repository.open(treeWalk.getObjectId(0))
        return new String(objectLoader.bytes)
    }
}
