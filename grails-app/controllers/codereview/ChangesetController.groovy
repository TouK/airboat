package codereview

import testFixture.Fixture


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
        gitRepositoryService.checkoutProject(Fixture.PROJECT_REPOSITORY_URL)
    }


}
