package codereview

import testFixture.Fixture
import grails.converters.JSON

class ChangesetController {

    ScmAccessService scmAccessService

    def index() {
        def offset = "0"
        if(session.offset != null){          //TODO: change flag on method which compare this
            session["offset"]   = "0"        //TODO: change "0" and "offset" on variables
        } else {                                             //TODO check if it is used and if not - remove
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

    def nextTenChangesets = {       //TODO:refactor name getAnotherTenChangesets
        def offset = params.id     //TODO Why? It's actually not getting, but rendering - check view, javascript involved
        [offset: offset]
    }

    def withComments = {           //experimental

    }
}

