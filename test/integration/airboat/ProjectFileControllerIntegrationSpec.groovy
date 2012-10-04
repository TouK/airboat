package airboat

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

        def projectFileInFirstChangeset = buildThreadInChangesetWithPosition(Changeset.build(project: project), 13)
        def projectFileInSecondChangeset = buildThreadInChangesetWithPosition(Changeset.build(project: project), 42)

        expect:
        controller.snippetWithCommentsService != null

        when:
        def positions = controller.getThreadPositionsProperties(projectFileInFirstChangeset)

        then:
        positions*.lineNumber == projectFileInFirstChangeset.commentThreadsPositions*.lineNumber

        when:
        positions = controller.getThreadPositionsProperties(projectFileInSecondChangeset)

        then:
        positions*.lineNumber == projectFileInSecondChangeset.commentThreadsPositions*.lineNumber
    }

    private ProjectFileInChangeset buildThreadInChangesetWithPosition(Changeset changeset, int lineNumber) {
        def projectFileInChangeset = ProjectFileInChangeset.build(changeset: changeset, projectFile: projectFile)
        ThreadPositionInFile.build(
                projectFileInChangeset: projectFileInChangeset,
                'thread.comments': [comment],
                lineNumber: lineNumber
        )
        projectFileInChangeset
    }
}
