package codereview

import testFixture.Fixture

import grails.plugin.spock.IntegrationSpec

class GitRepositoryServiceIntegrationSpec extends IntegrationSpec {

    def gitRepositoryService

    def "should fetch changesets from project's repository"() {

        when:
            gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
            def changelog = gitRepositoryService.fetchAllChangesetsWithFiles(Fixture.PROJECT_REPOSITORY_URL)

        then:
            changelog == changelog.sort(false, { it.date.time })
            changelog.size() >= Fixture.LOWER_BOUND_FOR_NUMBER_OF_COMMITS
            changelog.first().author == Fixture.FIRST_COMMIT_AUTHOR
            changelog.first().date == Fixture.FIRST_COMMIT_DATE
    }

    def "should get the name of first changed file in codereview project"() {
        when:
            gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changedFiles = gitRepositoryService.getFileNamesFromChangeSetsList(changes)

        then:
            changedFiles[0][0]?.getName() != null
    }
    //TODO add test for fetchFullChangelog when in case project was not been checked out
    def "should return getFiles"(){
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def change = changes[0]
            def changedFiles = gitRepositoryService.getFileNamesFromGitChangeSet(change)

        then:
            changedFiles.size() != 0
            changedFiles[0].getName() != null



        //changedFiles[0].getName() == "filePath"
        //changedFiles == [0, 1, 2]
    }
    def "should create changesets with added files" () {
        when:

            def changes = gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL)
            def changesetsWithFiles = gitRepositoryService.createChangesetsWithFiles(changes)

        then:
            changesetsWithFiles !=  null
            //changesetsWithFiles[-1].projectFiles.size() != 0
            //changesetsWithFiles[-1].projectFiles.size() == 2
           // changesetsWithFiles[-1].projectFiles["name"][0] =~ "/"
    }
}


