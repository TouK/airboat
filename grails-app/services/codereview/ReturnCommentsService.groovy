package codereview

import grails.plugins.springsecurity.Secured

class ReturnCommentsService {

    def springSecurityService

    def getCommentJSONproperties(UserComment userComment) {
        def commentProperties = userComment.properties + [
                author: userComment.author.username,
                dateCreated: userComment.dateCreated.format('yyyy-MM-dd HH:mm')
        ]
        commentProperties.keySet().retainAll('text', 'author', 'dateCreated')
        commentProperties
    }
}
