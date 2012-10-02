import grails.plugins.springsecurity.SpringSecurityService
import airboat.ProjectFile
import airboat.CommentThread
import airboat.ProjectFileInChangeset
import airboat.ChangeType

def encodePasswordMock = { password, salt = null -> "#encoded#password=${password}#salt=${salt}" }

testDataConfig {
    sampleData {
        'airboat.Project' {
            def (nameCounter, urlCounter) = [1, 1]
            name = {-> "project_name_${nameCounter++}" }
            url = {-> "git://git.touk.pl/project_${urlCounter++}" }
        }

        'airboat.Changeset' {
            def i = 1
            identifier = {-> "identifier_${i++}" }
        }

        'airboat.User' {
            def i = 1
            username = {-> "user_${i++}@test.com"}
            springSecurityService = {->
                [ encodePassword: encodePasswordMock] as SpringSecurityService
            }
        }

        'airboat.Commiter' {
            def i = 0
            cvsCommiterId = {->
                i++
                "Committer ${i}<committer_${i}@test.com>"
            }
        }

        'airboat.ProjectFileInChangeset' {
            changeType = ChangeType.ADD
        }

        //FIXME file a bug/enhancement request for same top of a diamond (in this case: Project in ThreadPositionInFile)
        'airboat.ThreadPositionInFile' {
            projectFileInChangeset = ProjectFileInChangeset.&build
            thread = CommentThread.&build
        }

    }
}

