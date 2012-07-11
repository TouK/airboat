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

    def initialCheckOut() {
        log.info("Checking out project.")
        gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
        redirect(action: "list", params: params)
    }

    def getLastChangesets = {
        render Changeset.list(max: 20, sort: "date", order: "desc") as JSON
    }

}

