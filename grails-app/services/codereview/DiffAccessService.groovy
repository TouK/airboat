package codereview

import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit

class DiffAccessService {


    String getDiffBetweenCommits(String oldHash, String newHash, String gitWorkingDirectory) {      //.git
        Repository repository = getRepositoryFromWorkingDirectory(gitWorkingDirectory)
        Git git = new Git(repository);
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        List<DiffEntry> diffs= git.diff()
                .setNewTree(getTreeIterator(repository, newHash))
                .setOldTree(getTreeIterator(repository, oldHash))
                .setOutputStream(baos)
                .call();
        return baos.toString()
    }

    Repository getRepositoryFromWorkingDirectory(String gitWorkingDirectory) {
        File gitDir = new File(gitWorkingDirectory)
        if (!gitDir.exists()) {
            throw new IllegalArgumentException("Can't find git working directory and access revision.")
        }
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder()
        Repository repository = repositoryBuilder.setGitDir(gitDir) // --git-dir if supplied, no-op if null
                .readEnvironment() // scan environment GIT_* variables
                .build()
        return repository
    }

    def getTreeIterator(Repository repository, String hash) {
        ObjectId commitId = repository.resolve(hash + "^{tree}");
        CanonicalTreeParser treeIterator = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
       if(commitId!= null) {
        treeIterator.reset(reader, commitId);
        return treeIterator
       }
        else {
           return null
       }
    }

    String getDiffComparingToPreviousCommit(String hash, String gitWorkingDirectory) {
        String hashOfPreviousCommit = hash + "^1"
        return getDiffBetweenCommits(hashOfPreviousCommit, hash, gitWorkingDirectory)
    }

    String getDiffToProjectFile(ProjectFile projectFile, String projectWorkingDirectory) {
        String gitWorkingDirectory = projectWorkingDirectory
        if(!projectWorkingDirectory.contains("/.git"))  {
         gitWorkingDirectory += "/.git"
        }

        String changesetDiff = getDiffComparingToPreviousCommit(projectFile.changeset.identifier, gitWorkingDirectory)
        return extractDiffForFileFromGitDiffCommandOutput(changesetDiff, projectFile.name)
    }

    String extractDiffForFileFromGitDiffCommandOutput(String diff, String fileName)  {
        def fileDiff = []
        diff.split("diff --git").each { if(it.contains(fileName)) {fileDiff.add(it.split("\n")[4..-1].join("\n") + "\n")}}
        return fileDiff.join("\n")
    }

    def getChangedFilesToCommit(String gitWorkingDirectory, String hash) {
        Repository repository = getRepositoryFromWorkingDirectory(gitWorkingDirectory)
        Git git = new Git(repository);
        def oldTree = getTreeIterator(repository, hash + "^1")
        def newTree =   getTreeIterator(repository, hash)
        if(newTree != null && oldTree != null)   {
        List<DiffEntry> diffs= git.diff()
                .setNewTree(newTree)
                .setOldTree(oldTree)

                .call()
        diffs.collect {   changedFile ->
            if (!changedFile.newPath.contains("null") ) {
            [name: changedFile.newPath]
            }
            else {
                [name: changedFile.oldPath]
            }
        }
        }
        else {
            return null
        }
    }
}
