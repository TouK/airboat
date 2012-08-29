package codereview

class LineComment {
    Integer lineNumber
    String text
    Date dateCreated
    static belongsTo = [author: User, projectFile: ProjectFile]

    LineComment(User author, Integer lineNumber, String text) {
        this.lineNumber = lineNumber
        this.text = text
        author.addToLineComments(this)
    }

    static constraints = {
        text maxSize: 4096
        text blank: false
    }
}
