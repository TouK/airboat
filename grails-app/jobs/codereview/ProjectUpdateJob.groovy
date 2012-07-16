package codereview

class ProjectUpdateJob {

    ChangelogAccessService changelogAccessService

    static triggers = {
      simple repeatInterval: 10000l // execute job once in 10 seconds
    }

    def execute() {
        update()
    }

    def update() {
        //deleteAllChangesets()
        changelogAccessService.fetchChangelogAndSave("git@git.touk.pl:touk/codereview.git")
    }

    private void deleteAllChangesets() {
        Changeset.createCriteria().list { ge("date", new Date(0)) }*.delete() //couldn't find an easier way with H2...
    }
}
