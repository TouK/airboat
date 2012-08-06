package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture
import spock.lang.Ignore

@Mock([Changeset, ProjectFile])
class ProjectFileAccessServiceSpec extends Specification{
    def "when given project and file name and file doesn't exist should return info about it"() {
        given:
        def fileName = "idontexist.txt"
        def projectPath = "."
        ProjectFileAccessService projectFileAccessService  = new ProjectFileAccessService()
        def content

        when:
        content = projectFileAccessService.fetchFileContentFromPath(projectPath, fileName)

        then:
        content != null
        content == "File doesn't exist"
    }

    def "should return file content if file exists"() {
        given:
        def fileName = Fixture.PATH_TO_FILE_PRESENT_IN_FIRST_COMMIT
        def projectPath = "."
        ProjectFileAccessService projectFileAccessService  = new ProjectFileAccessService()

        when:
        def content = projectFileAccessService.fetchFileContentFromPath(projectPath, fileName)

        then:
        content != null
        content != "File doesn't exist"
        content.contains("grails.mime.file.extensions = true")
    }

    @Ignore
    def "how should it behave if asked for not a text file"() {
      //  given:
      //
      //  when:
      //
      //
      //  then:
    }
}
