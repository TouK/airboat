import codereview.Changeset
import grails.converters.JSON
import codereview.Project
import testFixture.Fixture

class BootStrap {

    def init = { servletContext ->

        JSON.registerObjectMarshaller(Changeset) {
            def returnMap = [:]
            returnMap['identifier'] = it.identifier
            returnMap['author'] = it.author
            returnMap['date'] = it.date
            returnMap['email'] = it.getEmail()
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
