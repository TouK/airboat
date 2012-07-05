import codereview.Changeset
import codereview.ChangesetImporter
import codereview.GitRepository

class BootStrap {

    def init = { servletContext ->
        new ChangesetImporter(new GitRepository()).importFrom("git@git.touk.pl:touk/codereview.git")
    }
    def destroy = {
    }
}
