import grails.plugins.springsecurity.SpringSecurityService
import codereview.ProjectFile
import codereview.CommentThread
import codereview.ProjectFileInChangeset
import codereview.ChangeType

def encodePasswordMock = { password, salt = null -> "#encoded#password=${password}#salt=${salt}" }

testDataConfig {
    sampleData {
        'codereview.Project' {
            def (nameCounter, urlCounter) = [1, 1]
            name = {-> "project_name_${nameCounter++}" }
            url = {-> "git://git.touk.pl/project_${urlCounter++}" }
        }

        'codereview.Changeset' {
            def i = 1
            identifier = {-> "identifier_${i++}" }
        }

        'codereview.User' {
            def i = 1
            username = {-> "user_${i++}@test.com"}
            springSecurityService = {->
                [ encodePassword: encodePasswordMock] as SpringSecurityService
            }
        }

        'codereview.Commiter' {
            def i = 0
            cvsCommiterId = {->
                i++
                "Committer ${i}<committer_${i}@test.com>"
            }
        }

        'codereview.ProjectFileInChangeset' {
            changeType = ChangeType.ADD
        }

        //FIXME file a bug/enhancement request for same top of a diamond (in this case: Project in ThreadPositionInFile)
        'codereview.ThreadPositionInFile' {
            projectFileInChangeset = ProjectFileInChangeset.&build
            thread = CommentThread.&build
        }

    }
}

