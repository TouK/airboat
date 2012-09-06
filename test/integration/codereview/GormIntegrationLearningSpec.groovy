package codereview

import grails.plugin.spock.IntegrationSpec

import grails.validation.ValidationException

class GormIntegrationLearningSpec extends IntegrationSpec {

    def 'should fail on errors when it is said so in config (it should be)'() {
        when:
        invalidProject().save()

        then:
        thrown(ValidationException)
    }

    def 'findOrSaveWhere will throw exceptions for invalid domain instances iff default failOnError set in Config'() {
        given:
        def invalid = invalidProject()

        when:
        Project.findOrSaveWhere(name: invalid.name, url: invalid.url)

        then:
        thrown(ValidationException)
    }

    private Project invalidProject() {
        new Project()
    }

    def "should cascade saves from Commmitter to Changeset"() {
        given:
        Project project = Project.build().save(flush: true)
        Commiter committer = Commiter.buildWithoutSave()
        Changeset.buildWithoutSave(commiter: committer, project: project)

        expect:
        committer.changesets*.commiter == [committer]

        when:
        committer.save()

        then:
        Commiter.count() == 1
        Changeset.count() == 1
    }

}