package codereview

import grails.plugin.spock.IntegrationSpec

class ProjectFileControllerIntegrationSpec extends IntegrationSpec {

    def scmAccessService
    def snippetWithCommentsService
    def diffAccessService

    ProjectFileController controller = new ProjectFileController(
            scmAccessService: scmAccessService,
            snippetWithCommentsService: snippetWithCommentsService,
            diffAccessService: diffAccessService
    )

    def 'should return Comments for ProjectFile with their position in given Changeset'() {
        given:
        def fileName = 'groovy.groovy'
        Project project = Project.build()
        ProjectFile projectFile = ProjectFile.buildWithoutSave(name: fileName, project: project)
        LineComment comment = LineComment.build(text: 'first comment')

        Changeset firstChangeset = Changeset.build(projectFiles: [projectFile], project: project)
        Changeset secondChangeset = Changeset.build(projectFiles: [projectFile], project: project)

        LineCommentPosition.build(changeset: firstChangeset, projectFile: projectFile, comment: comment, lineNumber: 13)
        LineCommentPosition.build(changeset: secondChangeset, projectFile: projectFile, comment: comment, lineNumber: 42)

        when:
        def comments = controller.getLineComments(firstChangeset, projectFile)

        then:
        comments*.lineNumber == firstChangeset.lineCommentsPositions*.lineNumber

        when:
        comments = controller.getLineComments(secondChangeset, projectFile)

        then:
        comments*.lineNumber == secondChangeset.lineCommentsPositions*.lineNumber
    }
}
