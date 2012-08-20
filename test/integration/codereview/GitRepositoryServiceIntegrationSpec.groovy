package codereview


import testFixture.Fixture
import grails.plugin.spock.IntegrationSpec
import spock.lang.Ignore


class GitRepositoryServiceIntegrationSpec extends IntegrationSpec{

    GitRepositoryService gitRepositoryService

    def "should extract project name from scmUrl" () {
        when:
        def projectName = gitRepositoryService.getProjectNameFromScmUrl(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def projectName2 = gitRepositoryService.getProjectNameFromScmUrl("git://home/projects/kaboom.git")
        then:
        projectName == "codereview"
        projectName2 == "kaboom"
    }

    def "should set repo  if it isn't set already"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        then:
        true
    }

    @Ignore
    def "should update repo by pulling"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def updateResult = gitRepositoryService.updateRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        then:
        updateResult.success == true
    }

    @Ignore
    def "should get all changesets" () {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changesets = gitRepositoryService.getAllChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        then:
        !changesets.isEmpty()
        changesets.size > 0
        changesets[1].files != null
    }

    @Ignore
    def "should get only newer changesets"() {
        when:
        gitRepositoryService.createRepository(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        def changesets = gitRepositoryService.getAllChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL)
        int index = changesets.size()/10
        String hash = changesets[index].rev
        def newerChangesets = gitRepositoryService.getNewChangesets(Fixture.PROJECT_CODEREVIEW_REPOSITORY_URL, hash)
        then:
        !newerChangesets.isEmpty()
        newerChangesets.size() < changesets.size()
        newerChangesets.last().authorEmail
        newerChangesets.last().fullMessage
    }
}