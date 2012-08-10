package codereview

import org.spockframework.missing.ControllerIntegrationSpec

class ProjectFileControllerIntegrationSpec extends ControllerIntegrationSpec {

    def "should return comments"() {
        given:
        def fileName = 'groovy.groovy'
        Project project = Project.build()

        Changeset firstChangeset = Changeset.build(project: project)
        ProjectFile firstProjectfile = ProjectFile.build(changeset: firstChangeset, name: fileName)
        LineComment firstLinecomment = LineComment.build(projectFile: firstProjectfile, text: 'first comment')

        Changeset secondChangeset = Changeset.build(project: project)
        ProjectFile secondProjectfile = ProjectFile.build(changeset: secondChangeset, name: fileName)
        LineComment secondLinecomment = LineComment.build(projectFile: secondProjectfile, text: 'second comment')

        when:
        def comments =  controller.getLineComments(firstProjectfile.name, project.name)

        then:
        comments == [firstLinecomment, secondLinecomment]
    }
}
