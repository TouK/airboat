package codereview

import grails.converters.JSON
import grails.plugin.spock.ControllerSpec
import testFixture.Fixture



class ChangesetControllerIntegrationSpec extends ControllerSpec {

    def setup() {
        JSON.registerObjectMarshaller(Changeset, { Changeset changeset -> [id: changeset.id, identifier: changeset.identifier] })
    }

    def "getNextFewChangesetsOlderThan() should return few next changesets older one with given revision id as JSON"() {

        given:
        def latestChangesetId = "hash25"
        def testProjectName = "codereview"
        def testProject = new Project(testProjectName,Fixture.PROJECT_REPOSITORY_URL)
        testProject.addToChangesets(new Changeset(latestChangesetId, "kpt", "", new Date(3)))
        testProject.addToChangesets(new Changeset("hash24", "kpt", "", new Date(2)))
        testProject.addToChangesets(new Changeset("hash23", "agj", "", new Date(1)))
        testProject.save()

        when:
        controller.params.projectName =  testProjectName
        controller.params.changesetId = latestChangesetId
        controller.getNextFewChangesetsOlderThan()

        then:
        def responseChangesets = controller.response.json
        responseChangesets != null
        responseChangesets[0].identifier == "hash23"
        responseChangesets[1].identifier == "hash24"
    }


}
