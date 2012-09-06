package codereview

class LineComment {

    String text
    Date dateCreated
    static belongsTo = [author: User, thread: CommentThread]

    LineComment(User author, String text) {
        this.text = text
        author.addToLineComments(this)
    }

    static constraints = {
        text maxSize: 4096
        text blank: false
    }
}
