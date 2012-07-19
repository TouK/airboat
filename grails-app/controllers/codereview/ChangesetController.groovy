package codereview

import testFixture.Fixture
import grails.converters.JSON

class ChangesetController {

    ChangelogAccessService changelogAccessService
    GitRepositoryService gitRepositoryService

    def index() {
        def offset = "0"
        if(session.offset != null){
            session["offset"]   = "0"
        }
        else {
            session.setAttribute("offset", offset)
        }
      [offset: offset]

    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [changesetInstanceList: Changeset.list(params), changesetInstanceTotal: Changeset.count()]
    }
    /**
     * TODO helper method for development phase
     */
    def initialCheckOut() {
        log.info("Checking out project.")
        gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
        redirect(action: "index", params: params)
    }

    def getLastChangesets = {
        render Changeset.list(max: 21, sort: "date", order: "desc") as JSON
    }
    def getLastChangeset = {
        render Changeset.list(max: 1, sort: "date", order: "desc") as JSON

    }
    def getChangeset = {
        def id = params.id
        def changeset = Changeset.findById(params.id)
        def changesetList = [changeset]
        render changesetList as JSON
    }
    def getFileNamesForChangeset = {      //needs id of changeset
        def changeset = Changeset.findById(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        render files as JSON
    }
    def getNextTenChangesets = {
        def id = params.id
        def myOffset = id.toInteger()*10 +1
        render Changeset.list(max: 10, sort: "date", order: "desc", offset: myOffset) as JSON
    }
    def nextTenChangesets = {

        def offset =   params.id

        [offset: offset]
    }

}

