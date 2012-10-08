package airboat

class LineComment {

    String text
    boolean isArchived
    Date dateCreated

    static belongsTo = [author: User, thread: CommentThread]

    static constraints = {
        text blank: false, maxSize: 4096
    }

    LineComment(User author, String text) {
        this.text = text
        isArchived = false
        author.addToLineComments(this)
    }
}
