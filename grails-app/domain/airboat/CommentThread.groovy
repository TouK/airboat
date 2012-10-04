package airboat

class CommentThread {

    static hasMany = [comments: LineComment]

    static constraints = {
        comments minSize: 1
    }

    static mapping = {
        comments fetch: 'join', sort: 'dateCreated', order: 'asc'
    }

    CommentThread(LineComment comment) {
        addToComments(comment)
    }

    def getCreationDate() {
        comments*.dateCreated.min()
    }
}
