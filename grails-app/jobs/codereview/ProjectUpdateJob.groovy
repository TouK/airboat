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
        changelogAccessService.fetchChangelogAndSave("git@git.touk.pl:touk/codereview.git")
    }
}
