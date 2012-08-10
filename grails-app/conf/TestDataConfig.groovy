import grails.plugins.springsecurity.SpringSecurityService

def encodePasswordMock = { password, salt = null -> "#encoded#password=${password}#salt=${salt}" }

testDataConfig {
    sampleData {
        'codereview.Project' {
            def (nameCounter, urlCounter) = [1, 1]
            name = {-> "name_${nameCounter++}" }
            url = {-> "url_${urlCounter++}" }
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

        'codereview.LineComment'{
            def i = 1
            lineNumber = {-> i++}
            text = {-> "tekst=${i++}"}
            author = {-> "author=${i++}"}
        }
    }
}

