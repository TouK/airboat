package codereview

import testFixture.Fixture
import grails.converters.JSON

class ChangesetController {

    ScmAccessService scmAccessService

    def index() {
        def offset = "0"
        if(session.offset != null){
            session["offset"]   = "0"
        } else {
            session.setAttribute("offset", offset)
        }
        [offset: offset]
    }

    /**
     * TODO helper method for development phase
     */
    def initialCheckOut() {
        log.info("Checking out project.")
        scmAccessService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
        redirect(action: "index", params: params)
    }

    def getLastChangesets = {
        render Changeset.list(max: 21, sort: "date", order: "desc") as JSON
    }

    def getFileNamesForChangeset = {      //needs id of changeset
        def changeset = Changeset.findById(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        render files as JSON
    }

    def getNextTenChangesets = {
        def id = params.id
        def myOffset = id.toInteger() * 10 + 1
        render Changeset.list(max: 10, sort: "date", order: "desc", offset: myOffset) as JSON
    }

    def nextTenChangesets = {
        def offset = params.id
        [offset: offset]
    }

    def sendJSON = {
    }
    def withComments = {           //experimental

    }
}

