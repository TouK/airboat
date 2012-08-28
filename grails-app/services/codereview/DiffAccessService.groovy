package codereview

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.errors.RepositoryNotFoundException

class DiffAccessService {

    def infrastructureService

    String getDiffWithPreviousRevisionFor(Changeset changeset, ProjectFile projectFile) {
        File projectRoot = infrastructureService.getProjectRoot(changeset.project.url)
        String changesetDiff = getDiffWithPreviousCommit(projectRoot, changeset.identifier)
        return extractDiffForFileFromGitDiffCommandOutput(changesetDiff, projectFile.name)
    }

    String getDiffWithPreviousCommit(File projectRoot, String pathSpec) {
        return getDiffBetweenCommits(projectRoot, "$pathSpec^1", pathSpec)
    }

    private String getDiffBetweenCommits(File projectRoot, String oldPathSpec, String newPathSpec) {
        Repository repository = openGitRepository(projectRoot)
        Git git = new Git(repository);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        List<DiffEntry> diffs = git.diff()
                .setOldTree(getTreeIterator(repository, oldPathSpec))
                .setNewTree(getTreeIterator(repository, newPathSpec))
                .setOutputStream(outputStream)
                .call();
        return outputStream.toString()
    }

    private String extractDiffForFileFromGitDiffCommandOutput(String diff, String fileName) {
        def fileDiff = []
        diff.split("diff --git").each {
            if (it.contains(fileName)) {
                fileDiff.add(it.split("\n")[4..-1].join("\n") + "\n")
            }
        }
        return fileDiff.join("\n")
    }

    List<GitChangedFile> getFilesChangedInCommit(File workTree, String pathSpec) {
        Repository repository = openGitRepository(workTree)
        Git git = new Git(repository);
        def oldTree = getTreeIterator(repository, pathSpec + "^1")
        def newTree = getTreeIterator(repository, pathSpec)
        if (newTree != null && oldTree != null) {
            List<DiffEntry> diffs = git.diff()
                    .setOldTree(oldTree)
                    .setNewTree(newTree)
                    .call()
            diffs.collect { changedFile ->
                if (!changedFile.newPath.contains("null")) {
                    new GitChangedFile(name: changedFile.newPath, changeType: changedFile.changeType)
                }
                else {
                    new GitChangedFile(name: changedFile.oldPath, changeType: changedFile.changeType)
                }
            }
        } else {
            return null
        }
    }

    Repository openGitRepository(File workTree) throws RepositoryNotFoundException {
        return new RepositoryBuilder().setWorkTree(workTree).setMustExist(true).build()
    }

    private def getTreeIterator(Repository repository, String hash) {
        ObjectId commitId = repository.resolve(hash + "^{tree}");
        CanonicalTreeParser treeIterator = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
        if (commitId != null) {
            treeIterator.reset(reader, commitId);
            return treeIterator
        } else {
            return null
        }
    }
}
