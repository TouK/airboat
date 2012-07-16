package codereview



import testFixture.Fixture

import grails.plugin.spock.IntegrationSpec


class GetFilesNamesLearningIntegrationSpec extends  IntegrationSpec {
    def gitRepositoryService
     def "should return getFiles"(){
         when:
         def changes = gitRepositoryService.getGitChangeSets(Fixture.PROJECT_REPOSITORY_URL)
         def change = changes[0]
         def changedFiles = gitRepositoryService.getFileNamesFromGitChangeSet(change)
         then:
         changedFiles.size() != 0
         changedFiles[0].getName() != null
         changedFiles[0].getName() == "filePath"
     }


}
