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
        Project.all.each {
            log.info("Checking out project ${it.url}.")
            scmAccessService.checkoutProject(it.url)
        }
        redirect(views: '/index', params: params)
    }

    def getLastChangesets = {    //TODO: refactor me, please
        if(params.id == null){render Changeset.list(max: 21, sort: 'date', order: 'desc') as JSON}
        else{
        def project = Project.findByName(params.id) //TODO examine number of queries and try to make it 1.
        def projectQuery = Changeset.findAllByProject(project, [max: 21, sort: 'date', order: 'desc'])
        render  projectQuery   as JSON
        }
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
        def project = Project.findByName(params.id)  //change params

        def nextFewChangesets = Changeset.where {
             date < property(date).of { identifier == params.lastChangesetId}
        }.list(max: 10, sort: 'date', order: 'desc')
        render nextFewChangesets as JSON
    }
    def addLineComments = {
        [params: params]
    }
}

