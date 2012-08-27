package learning

import spock.lang.Specification
import testFixture.Fixture
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry

import org.eclipse.jgit.treewalk.filter.TreeFilter
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.DepthWalk
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.FileTreeIterator
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.AnyObjectId
import org.eclipse.jgit.lib.ObjectId
import spock.lang.Ignore

class JgitApiLearningSpec extends Specification {

    def "Should build repository object for yet-nonexistent directory" (){
        when:
        def gitDir = new File("/file/that/does/not/exist")
        def repositoryBuilder = new RepositoryBuilder()
        def repository = repositoryBuilder.setGitDir(gitDir).build()

        then:
        repository.directory == gitDir
        repository.isBare()
        repository.directory.exists() == false
    }

    @Ignore
    def "What repository properties are?"() {
        when:
        def gitDir = new File("/home/touk/codereview")
        def repositoryBuilder = new RepositoryBuilder()
        def repository = repositoryBuilder.setWorkTree(gitDir) // --git-dir if supplied, no-op if null
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()
        then:
        repository.additionalHaves.toString() == "[]"
        repository.branch.toString() == "master"
        repository.config.toString() == "FileBasedConfig[/home/touk/codereview/.git/config]"
    }

    @Ignore
    def "How to create git object"() {
        when:
        def gitDir = new File("/home/touk/codereview/.git")
        def outputFile2 = new File("/home/touk/diff-output2")
        def outputStream2 = new FileOutputStream(outputFile2)

        def repositoryBuilder = new RepositoryBuilder()
        def repository = repositoryBuilder.setWorkTree(gitDir) // --git-dir if supplied, no-op if null
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()
        Git git = new Git(repository);


        then:
        git != null


    }


    def "should get id of commit and read files from it"() {
        when:

        def gitDir = new File("/home/touk/codereview/.git")
        def repositoryBuilder = new RepositoryBuilder()
        def repository = repositoryBuilder.setWorkTree(gitDir) // --git-dir if supplied, no-op if null
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()
        Git git = new Git(repository);

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        String diffAnswer

        String oldHash = "6e4dab9d06c7378e98866a298221d2f2ad070b8c";
        String newHash =  "7c7b0e3401dbfe52a4d51c44f92bc930a8b34f56"

        ObjectId headId = repository.resolve(newHash + "^{tree}");
        ObjectId oldId = repository.resolve(oldHash + "^{tree}");

        ObjectReader reader = repository.newObjectReader();

        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldId);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, headId);

        List<DiffEntry> diffs= git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .setOutputStream(baos)
                .call();
        diffAnswer = baos.toString()

        then:
        diffAnswer.contains("diff --git")
        diffs[0].getPath(DiffEntry.Side.NEW) == "grails-app/views/changeset/index.gsp"
        diffs[0].getChangeType().toString() == "MODIFY"

    }

    def "how to extract diff to certain file from git command output"() {
        when:
        def gitDir = new File("/home/touk/codereview/.git")
        def repositoryBuilder = new RepositoryBuilder()
        def repository = repositoryBuilder.setWorkTree(gitDir) // --git-dir if supplied, no-op if null
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()
        Git git = new Git(repository);

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        String diffAnswer

        String oldHash = "7c7b0e3401dbfe52a4d51c44f92bc930a8b34f56^1"
        String newHash =  "7c7b0e3401dbfe52a4d51c44f92bc930a8b34f56"

        ObjectId headId = repository.resolve(newHash + "^{tree}")
        ObjectId oldId = repository.resolve(oldHash + "^{tree}")

        ObjectReader reader = repository.newObjectReader();

        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser()
        oldTreeIter.reset(reader, oldId)
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser()
        newTreeIter.reset(reader, headId)

        List<DiffEntry> diffs= git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .setOutputStream(baos)
                .call();
        diffAnswer = baos.toString()

        def firstFileDiff = diffAnswer.split("diff --git")[1]

        then:
        firstFileDiff != null
        firstFileDiff != ""
    }
}



