package airboat

class CommentConverterService {

    def springSecurityService

    //this should handle both UserComment and LineComment
    //TODO (UserComment and LineComment should be one class or at least have a common interface, btw)
    def getCommentJSONproperties(def comment) {
        def commentProperties = comment.properties + [
                author: comment.author.username,
                dateCreated: comment.dateCreated.format('yyyy-MM-dd HH:mm')
        ]
        commentProperties.keySet().retainAll('text', 'author', 'dateCreated')
        commentProperties
    }
}
