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
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder()
        Repository repository = repositoryBuilder.setWorkTree(gitDir) // --git-dir if supplied, no-op if null
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()
        return repository
    }

    def getTreeIterator(Repository repository, String hash) {
        ObjectId commitId = repository.resolve(hash + "^{tree}");
        CanonicalTreeParser treeIterator = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
        treeIterator.reset(reader, commitId);
        return treeIterator
    }

    String getDiffComparingToPreviousCommit(String hash, String gitWorkingDirectory) {
        String hashOfPreviousCommit = hash + "^1"
        return getDiffBetweenCommits(hashOfPreviousCommit, hash, gitWorkingDirectory)
    }

    String getDiffToProjectFile(ProjectFile projectFile, String projectWorkingDirectory) {
        String gitWorkingDirectory = projectWorkingDirectory +"/.git" //something like that, hopefully
        String changesetDiff = getDiffComparingToPreviousCommit(projectFile.changeset.identifier, gitWorkingDirectory)
        return extractDiffForFileFromGitDiffCommandOutput(changesetDiff, projectFile.name)
    }

    String extractDiffForFileFromGitDiffCommandOutput(String diff, String fileName)  {
        def fileDiff = []
        diff.split("diff --git").each { if(it.contains(fileName)) {fileDiff.add(it.split("\n")[4..-1].join("\n") + "\n")}}
        return fileDiff.join("\n")
    }
}
