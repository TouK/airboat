package codereview


class ProjectUpdateJob {


    ChangesetImportingService changesetImportingService

    static triggers = {
      simple repeatInterval: 10000l // execute job once in 10 seconds
    }

    ProjectUpdateJob() {
        this.changesetImportingService = new ChangesetImportingService()
    }

    def execute() {
        print "Job run!"
        update()
    }

    def update() {

        changesetImportingService.importFrom("git@git.touk.pl:touk/codereview.git")
    }

    private void deleteAllChangesets() {
        Changeset.createCriteria().list { ge("date", new Date(0)) }*.delete() //couldn't find an easier way with H2...
    }
}
