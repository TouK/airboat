package codereview

import grails.buildtestdata.mixin.Build

import spock.lang.Specification

import org.eclipse.jgit.lib.Repository

import testFixture.JgitFixture

import org.eclipse.jgit.errors.RepositoryNotFoundException

import static testFixture.Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL
import spock.lang.Ignore

@Build([ProjectFile, Changeset])
class DiffAccessServiceSpec extends Specification {

    def diffAccessService = new DiffAccessService()
    String firstCodereviewHash = "ac464172cd45551eac74f4e5b19234ac4c77e3d7"
    String secondCodereviewHash = "7c7b0e3401dbfe52a4d51c44f92bc930a8b34f56"
    String projectWithoutRepositoryCloneUrl = "git://git.neverland.org/ohnoes"
    File projectRoot = new File(".")

    def setup() {
        diffAccessService.infrastructureService = Mock(InfrastructureService)
        def currentDirectory = new File(".")
        diffAccessService.infrastructureService.getProjectRoot(PROJECT_CODEREVIEW_REPOSITORY_URL) >> currentDirectory
        diffAccessService.infrastructureService.getProjectRoot(projectWithoutRepositoryCloneUrl) >> new File('notReallyAFile')
        assert new File(currentDirectory, ".git").isDirectory()
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
        Repository repository =  diffAccessService.openGitRepository(projectRoot)

        then:
        repository != null
        !repository.isBare()
    }

    def "should get difference between commit and it's parent" () {
        when:
        String diff = diffAccessService.getDiffWithPreviousCommit(projectRoot, firstCodereviewHash)

        then:
        diff != null
        diff != ""
        diff.contains("diff --git")
        diff.contains("index 0000000..740c3b2")
    }

    def "should get diff to project file"() {
        when:
        def project = Project.build(url: PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changeset = Changeset.build(identifier: secondCodereviewHash, project: project)
        def projectFile = ProjectFile.build(changesets: [changeset], name: "grails-app/views/changeset/index.gsp")
        String fileDiff = diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)

        then:
        fileDiff != null
        fileDiff.contains("+    <form class=\"add_comment\">")
        fileDiff.split("\n").size() > 5
    }


    def "what will happen if wrong directory path?" () {
        when:
        def project = Project.build(url: projectWithoutRepositoryCloneUrl)
        def changeset = Changeset.build(identifier: secondCodereviewHash, project: project)
        def projectFile = ProjectFile.build(changesets: [changeset], name: "grails-app/views/changeset/index.gsp")
        diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)

        then:
        thrown(RepositoryNotFoundException)
    }

    def "what will happen if given project file with incorrect file name?"() {
        when:
        def project = Project.build(url: PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changeset = Changeset.build(identifier: secondCodereviewHash, project: project)
        def projectFile = ProjectFile.build(changesets: [changeset], name: "grails-app/nothing")
        String fileDiff = diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)

        then:
        fileDiff == ""
    }

    //TODO check why it works differently in different environments
    @Ignore
    def "what will happen if given project file with incorrect changeset hash?"() {
        given:
        def project = Project.build(url: PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changeset = Changeset.build(identifier: "what?", project: project)
        def projectFile = ProjectFile.build(changesets: [changeset], name: "grails-app/views/changeset/index.gsp")

        when:
        String fileDiff = diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)

        then:
        fileDiff == ""
    }

}
