package codereview

class CommentThread {

    static hasMany = [comments: LineComment]

    CommentThread(LineComment comment) {
        addToComments(comment)
    }

    static constraints = {
        comments minSize: 1
    }
}
