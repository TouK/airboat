package airboat

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Changeset)
@Build(ProjectFileInChangeset)
class ProjectFileInChangesetConstraintsSpec extends Specification {

    def setup() {
        mockForConstraintsTests(Changeset)
    }

    @Unroll("Field '#field' of class Changeset should have constraint '#constraint' violated by value '#violatingValue'")
    def 'Changeset should have well defined constraints:'() {

        when:
        def changeset = Changeset.build()
        changeset."$field" = violatingValue

        then:
        changeset.validate() == false
        changeset.errors[field].toString() == constraint

        where:
        field           | constraint | violatingValue
        'identifier'    | 'blank'    | ''
        'identifier'    | 'nullable' | null
        'commitMessage' | 'nullable' | null
        'date'          | 'nullable' | null
    }

    def 'Changeset and its associated ProjectFiles must have the same Project'() {
        given:
        ProjectFile projectFile = ProjectFile.build()
        Changeset changeset = Changeset.build()

        expect:
        changeset.project != projectFile.project

        when:
        def projectFileInChangeset = new ProjectFileInChangeset(changeset, projectFile, ChangeType.ADD)

        then:
        !projectFileInChangeset.validate()
        projectFileInChangeset.errors.getFieldError('changeset').code == 'changesetsProjectFilesMustBeOfSameProject'
        projectFileInChangeset.errors.getFieldError('changeset').arguments == [
                'changeset', ProjectFileInChangeset, changeset,
                changeset.project.name, projectFile, projectFile.project.name
        ]
    }
}
