package codereview

import testFixture.Fixture
import grails.converters.JSON

class ChangesetController {

    ScmAccessService scmAccessService

    def index() {
    }

    /**
     * TODO helper method for development phase
     */
    def initialCheckOut() {
        log.info('Checking out project.')
        scmAccessService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
        redirect(views: '/index', params: params)
    }

    def getLastChangesets = {
        render Changeset.list(max: 21, sort: 'date', order: 'desc') as JSON
    }


    def getFileNamesForChangeset = {
        def changeset = Changeset.findByIdentifier(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        render files as JSON
    }


    def getChangeset = {
        def changeset = Changeset.findByIdentifier(params.id)
        def changesetList = [changeset]
        render changesetList as JSON
    }

    def getNextFewChangesetsOlderThan = {
        def nextFewChangesets = Changeset.where {
            date < property(date).of { identifier == params.id }
        }.list(max: 10, sort: 'date', order: 'desc')
        render nextFewChangesets as JSON
    }
}

