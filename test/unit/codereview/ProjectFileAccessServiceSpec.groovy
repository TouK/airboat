package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture
import org.apache.maven.scm.ChangeSet

@Mock([Changeset, ProjectFile])
class ProjectFileAccessServiceSpec extends Specification{
    def "when given project and file name and file doesn't exist should return info about it"() {
        given:
        def fileName = "idontexist.txt"
        def projectPath = '/tmp/projekt/'
        ProjectFileAccessService projectFileAccessService  = new ProjectFileAccessService()
        def content

        when:
        content = projectFileAccessService.fetchFileContentFromPath(fileName, projectPath)

        then:
        content != null
        content == "File doesn't exist"
    }

    def "should return file content if file exists"() {
        given:
        def fileName = "grails-app/conf/Config.groovy"
        def projectPath = '/tmp/projekt/'
        ProjectFileAccessService projectFileAccessService  = new ProjectFileAccessService()
        def content

        when:
        content = projectFileAccessService.fetchFileContentFromPath(fileName, projectPath)

        then:
        content != null
        content != "File doesn't exist"
        content.contains("grails.mime.file.extensions = true")
    }


    def "how should it behave if asked for not a text file"() {
      //  given:
      //
      //  when:
      //
      //
      //  then:
    }
}
