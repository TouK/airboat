import codereview.Changeset
import grails.converters.JSON

class BootStrap {

    def init = { servletContext ->

        JSON.registerObjectMarshaller(Changeset) {
            def returnMap = [:]
            returnMap['identifier'] = it.identifier
            returnMap['author'] = it.commiter.cvsCommiterId
            returnMap['date'] = it.date
            returnMap['email'] = it.commiter.getEmail()
            returnMap['commitComment'] = it.commitComment
            returnMap['id'] = it.id
            returnMap['commentsCount'] = it.commentsCount()
            return returnMap
        }

    }

    def destroy = {
    }
}
