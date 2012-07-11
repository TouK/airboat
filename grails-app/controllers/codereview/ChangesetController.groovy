package codereview

import testFixture.Fixture
import grails.converters.JSON

class ChangesetController {

    ChangelogAccessService changelogAccessService
    GitRepositoryService gitRepositoryService

    def index() {
        redirect(action: "list", params: params)
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

    def getChangesetFromDatabase() {       //TODO: pobrać z bazy  i wyświetlić
        HashMap jsonMap = new HashMap()
        List<Changeset> changesetFromDatabase = Changeset.list()

        def tempList  = changesetFromDatabase.collect {uniqueChangeset ->
            return [identifier: uniqueChangeset.identifier, author: uniqueChangeset.author, date:uniqueChangeset.date ]
        } .findAll{it.author = "ww"} .sort{it.date} .reverse()


        jsonMap.changeset    = tempList.subList(0,5)

    render jsonMap as JSON
    }


}
