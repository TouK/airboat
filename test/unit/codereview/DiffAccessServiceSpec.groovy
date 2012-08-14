package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.apache.maven.scm.ChangeSet
import spock.lang.Specification
import codereview.Project
import codereview.Changeset
import codereview.ProjectFile
import codereview.Commiter
import codereview.User
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.lib.ObjectReader
import testFixture.JgitFixture
import org.junit.Test

@Build([ProjectFile, Changeset])
@Mock([Project, Changeset, ProjectFile, Commiter, User])

class DiffAccessServiceSpec extends Specification{

    def diffAccessService
    String firstCodereviewHash
    String secondCodereviewHash
    String pathToGitWorkingDirectory
    String pathToGitWorkingDirectory2

    def setup() {
        diffAccessService = new DiffAccessService()
        firstCodereviewHash =  "ac464172cd45551eac74f4e5b19234ac4c77e3d7"
        secondCodereviewHash = "7c7b0e3401dbfe52a4d51c44f92bc930a8b34f56"
        pathToGitWorkingDirectory ="./.git"
        pathToGitWorkingDirectory2 = "."
    }

    def "should extract diff for file which name we've passed" () {
        when:
        String fileName = "/grails-app/controllers/codereview/ChangesetController.groovy"
        String correctGitDiffOutput = JgitFixture.CORRECT_GIT_DIF_OUTPUT
        def fileDiff = diffAccessService.extractDiffForFileFromGitDiffCommandOutput(correctGitDiffOutput, fileName)
        then:
        fileDiff != null
        fileDiff.contains("@@ -36,8 +36,8 @@ class ChangesetController {")
        fileDiff.contains("+                    email: getUserEmail(changeset),")

    }

    def "should return correct git repository object given correct data" () {
        when:
        Repository repository =  diffAccessService.getRepositoryFromWorkingDirectory(pathToGitWorkingDirectory)

        then:
        repository != null
        !repository.isBare()
    }

    def "should get difference between commit and it's parent" () {
        when:
        String diff = diffAccessService.getDiffComparingToPreviousCommit(firstCodereviewHash, pathToGitWorkingDirectory)

        then:
        diff != null
        diff != ""
        diff.contains("diff --git")
        diff.contains("index 0000000..740c3b2")
    }

    def "should return tree iterators when given hash" () {
        when:
        Repository repository =  diffAccessService.getRepositoryFromWorkingDirectory(pathToGitWorkingDirectory)
        def treeIterator = diffAccessService.getTreeIterator(repository, firstCodereviewHash)

        then:
        treeIterator != null
    }

    def "should return diff between to commits with given hashes" () {
        when:
        String oldHash = secondCodereviewHash + "^1"
        String newHash =  secondCodereviewHash
        String diff = diffAccessService.getDiffBetweenCommits(oldHash, newHash, pathToGitWorkingDirectory)

        then:
        diff.contains("index 849e3d4..b56440f 100644")
    }

    def "should get diff to project file"() {
        when:
        def changeset = Changeset.build(identifier: secondCodereviewHash )
        def projectFile = ProjectFile.build(changeset: changeset, name: "grails-app/views/changeset/index.gsp")
        String fileDiff = diffAccessService.getDiffToProjectFile(projectFile, pathToGitWorkingDirectory2 )

        then:
        fileDiff != null
        fileDiff.contains("+    <form class=\"add_comment\">")
        fileDiff.split("\n").size() > 5
    }


    def "what will happen if wrong directory path?" () {
        when:
        def wrongDirectoryPath = "../../wrong"
        def changeset = Changeset.build(identifier: secondCodereviewHash )
        def projectFile = ProjectFile.build(changeset: changeset, name: "grails-app/views/changeset/index.gsp")
        diffAccessService.getDiffToProjectFile(projectFile, wrongDirectoryPath )


        then:
        thrown(IllegalArgumentException)

    }

    def "what will happen if given project file with incorrect file name?"() {
        when:
        def changeset = Changeset.build(identifier: secondCodereviewHash )
        def projectFile = ProjectFile.build(changeset: changeset, name: "grails-app/nothing")
        String fileDiff = diffAccessService.getDiffToProjectFile(projectFile, pathToGitWorkingDirectory2 )


        then:
        fileDiff == ""
    }

    @Test(expected=Exception.class)
    def "what will happen if given project file with incorrect changeset hash?"() {
        given:
        def changeset = Changeset.build(identifier: "what?" )
        def projectFile = ProjectFile.build(changeset: changeset, name: "grails-app/views/changeset/index.gsp")
        expect:
        notThrown(null)

        when:
        String fileDiff = diffAccessService.getDiffToProjectFile(projectFile, pathToGitWorkingDirectory )

        then:
        fileDiff == null
        thrown(java.lang.NullPointerException)
    }

}
