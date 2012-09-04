package codereview

import grails.plugin.spock.IntegrationSpec

class ProjectFileControllerIntegrationSpec extends IntegrationSpec {

    def scmAccessService
    def snippetWithCommentsService
    def diffAccessService

    ProjectFileController controller = new ProjectFileController(
            scmAccessService: scmAccessService,
            //snippetWithCommentsService: snippetWithCommentsService, //TODO somehow this does not set the field...
            diffAccessService: diffAccessService
    )

    def 'should return comments'() {
        given:
        controller.snippetWithCommentsService = snippetWithCommentsService
        def fileName = 'groovy.groovy'
        Project project = Project.build()

        Changeset firstChangeset = Changeset.build(project: project)
        ProjectFile firstProjectfile = ProjectFile.build(changeset: firstChangeset, name: fileName)
        LineComment firstLinecomment = LineComment.build(projectFile: firstProjectfile, text: 'first comment')

        Changeset secondChangeset = Changeset.build(project: project)
        ProjectFile secondProjectfile = ProjectFile.build(changeset: secondChangeset, name: fileName)
        LineComment secondLinecomment = LineComment.build(projectFile: secondProjectfile, text: 'second comment')

        expect:
        controller.snippetWithCommentsService != null

        when:
        def comments = controller.getLineComments(firstProjectfile)

        then:
        comments*.text == [firstLinecomment, secondLinecomment]*.text
    }
}
