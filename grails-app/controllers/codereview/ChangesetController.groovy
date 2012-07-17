package codereview

import testFixture.Fixture
import grails.converters.JSON

class ChangesetController {

    ChangelogAccessService changelogAccessService
    GitRepositoryService gitRepositoryService

    def index() {
        //redirect(action: "list", params: params)
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
        //render ProjectFile.list()  as JSON
    }
    def getLastChangeset = {
        render Changeset.list(max: 1, sort: "date", order: "desc") as JSON

    }
    def getChangeset = {
        def id = params.id
        //def changeset = Changeset.list(max: 20, sort: "date", order: "desc")
        //render changeset[id.toInteger()..id.toInteger()] as JSON

        def changeset = Changeset.findById(params.id)
        def changesetList = [changeset]
        render changesetList as JSON
    }
    def getFileNamesForChangeset = {      //needs id of changeset
        def changeset = Changeset.findById(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        render files as JSON
    }



}

