package codereview

class LineComment {
    Integer lineNumber
    String text
    Date dateCreated
    String author
    static belongsTo = [projectFile: ProjectFile]

    LineComment(Integer lineNumber, String text, String author) {
        this.lineNumber = lineNumber
        this.text = text
        this.author = author

    }
    static constraints = {
    }
}
