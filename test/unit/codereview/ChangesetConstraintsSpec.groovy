package codereview

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Changeset)
@Build([Changeset, ProjectFile])
class ChangesetConstraintsSpec extends Specification {


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
        'commitComment' | 'nullable' | null
        'date'          | 'nullable' | null
    }

    def 'commentsCount should be zero (not null) for a Changeset without UserComment-s'() {
        when:
        Changeset changeset = Changeset.build()

        then:
        changeset.userComments == null
        changeset.commentsCount == 0
    }

    def 'two Changeset-s in two Projects can have the same identifier'() {
        given:
        Changeset existingChangeset = Changeset.build()

        expect:
        Changeset.build(identifier: existingChangeset.identifier)
    }

    def 'two Changeset-s in one Project can not have the same identifier'() {
        given:
        Changeset existingChangeset = Changeset.build()

        when:
        Changeset changeset = Changeset.buildWithoutSave(
                project: existingChangeset.project, identifier: existingChangeset.identifier
        )

        then:
        changeset.validate() == false
        changeset.errors.getFieldError('identifier').code == 'unique'
    }

    def 'Changeset and its associated ProjectFiles must have the same Project'() {
        given:
        ProjectFile projectFile = ProjectFile.build()
        Changeset changeset = Changeset.build()

        expect:
        changeset.project != projectFile.project

        when:
        changeset.addToProjectFiles(projectFile)

        then:
        !changeset.validate()
        println(changeset.errors.getFieldError('projectFiles'))

        changeset.errors.getFieldError('projectFiles').code == 'changesetsProjectFilesMustBeInSameProject'
        changeset.errors.getFieldError('projectFiles').arguments == [
                'projectFiles', Changeset, changeset.projectFiles,
                changeset.project.name, [[projectFile.name, projectFile.project.name]]
        ]
    }
}
