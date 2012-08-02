import codereview.Changeset
import grails.converters.JSON
import codereview.Project
import testFixture.Fixture

import static codereview.ScmAccessService.getEmail

class BootStrap {

    //FIXME add a bootstrap test, errors here are too frequent...
    def init = { servletContext ->

        JSON.registerObjectMarshaller(Changeset) {
            def returnMap = [:]
            returnMap['identifier'] = it.identifier
            returnMap['author'] = it.commiter.cvsCommiterId
            returnMap['date'] = it.date
            returnMap['email'] = getEmail(it.commiter.cvsCommiterId) //FIXME use changeset.committer.user.email or equivalent ASAP
            returnMap['commitComment'] = it.commitComment
            returnMap['id'] = it.id
            returnMap['commentsCount'] = it.commentsCount()
            return returnMap
        }


        new Project("codereview", Fixture.PROJECT_REPOSITORY_URL).save(flush: true)
        new Project("cyclone", Fixture.PROJECT_CYCLONE_REPOSITORY_URL).save(flush: true)
        new Project("TPSA", Fixture.PROJECT_TPSA_REPOSITORY_URL).save(flush: true)


    }



    def destroy = {
    }
}
