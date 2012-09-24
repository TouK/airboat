package codereview

import grails.plugin.spock.IntegrationSpec
import org.eclipse.jgit.diff.DiffEntry
import testFixture.Fixture

import static testFixture.Fixture.*
import grails.plugins.springsecurity.SpringSecurityService

class MyCommentsAndChangesetsFilterServiceIntegrationSpec extends IntegrationSpec {

    SpringSecurityService springSecurityService
    MyCommentsAndChangesetsFilterService myCommentsAndChangesetsFilterService

    def setupSpec() {}

    def cleanupSpec() {}

    def "should call getLastFilteredChangesets"() { //created for inspecting hibernate sql log output
        given:
        def authenticatedUser = User.build(username: 'jsd@touk.pl')
        springSecurityService.reauthenticate(authenticatedUser.username)

        when:
        myCommentsAndChangesetsFilterService.getLastFilteredChangesets()

        then:
        true
    }


}