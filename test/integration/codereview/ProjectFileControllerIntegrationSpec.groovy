package codereview

import grails.plugin.spock.IntegrationSpec
import util.DbPurger

class ProjectFileControllerIntegrationSpec extends IntegrationSpec {

    def scmAccessService
    def snippetWithCommentsService
    def diffAccessService

    ProjectFileController controller = new ProjectFileController(
            scmAccessService: scmAccessService,
            //snippetWithCommentsService: snippetWithCommentsService, //TODO somehow this does not set the field...
            diffAccessService: diffAccessService
    )

    Project project
    ProjectFile projectFile
    LineComment comment

    def setup() {
        project = Project.build()
        projectFile = ProjectFile.buildWithoutSave(name: 'groovy.groovy', project: project)
        comment = LineComment.build(text: 'first comment')
    }

    def 'should return Comments for ProjectFile with their position in given Changeset'() {
        given:
        controller.snippetWithCommentsService = snippetWithCommentsService
        Changeset firstChangeset = Changeset.build(project: project)
        Changeset secondChangeset = Changeset.build(project: project)

        buildThreadWithPosition(firstChangeset, 13)
        buildThreadWithPosition(secondChangeset, 42)

        expect:
        controller.snippetWithCommentsService != null

        when:
        def comments = controller.getLineComments(firstChangeset, projectFile)

        then:
        comments*.lineNumber == firstChangeset.projectFilesInChangeset*.commentThreadsPositions.flatten()*.lineNumber

        when:
        comments = controller.getLineComments(secondChangeset, projectFile)

        then:
        comments*.lineNumber == secondChangeset.projectFilesInChangeset*.commentThreadsPositions.flatten()*.lineNumber
    }

    private void buildThreadWithPosition(Changeset changeset, int lineNumber) {
        def projectFileInChangeset = ProjectFileInChangeset.build(changeset: changeset, projectFile: projectFile)
        ThreadPositionInFile.build(
                projectFileInChangeset: projectFileInChangeset,
                'thread.comments': [comment],
                lineNumber: lineNumber
        )
    }
}