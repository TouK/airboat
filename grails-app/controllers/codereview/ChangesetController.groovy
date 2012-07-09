package codereview


class ChangesetController {

    ChangesetImportingService changesetImportingService

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [changesetInstanceList: Changeset.list(params), changesetInstanceTotal: Changeset.count()]
    }

    //TODO initial checkout
    def initialCheckOut() {

    }
    def updateFromRepository() {
        deleteAllChangesets()
        changesetImportingService.importFrom("git@git.touk.pl:touk/codereview.git")
        redirect(action: "list", params: params)
    }

    private void deleteAllChangesets() {
        Changeset.createCriteria().list { ge("date", new Date(0)) }*.delete() //couldn't find an easier way with H2...
    }
}
