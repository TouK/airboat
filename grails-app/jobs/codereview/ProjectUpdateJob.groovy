package codereview

class ProjectUpdateJob {

    ChangesetImportingService changesetImportingService

    static triggers = {
      simple repeatInterval: 10000l // execute job once in 10 seconds
    }

    def execute() {
        update()
    }

    def update() {
        changesetImportingService.importFrom("git@git.touk.pl:touk/codereview.git")
    }
}
