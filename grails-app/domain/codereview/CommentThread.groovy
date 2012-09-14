package codereview

class CommentThread {

    static hasMany = [comments: LineComment]

    static constraints = {
        comments minSize: 1
    }

    static mapping = {
        comments sort: 'dateCreated', order: 'asc'
    }

    CommentThread(LineComment comment) {
        addToComments(comment)
    }
}
