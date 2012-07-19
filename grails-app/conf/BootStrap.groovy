import codereview.Changeset
import grails.converters.JSON

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
            return returnMap
        }

    }

    def destroy = {
    }
}
