package codereview

class LineComment {
    Integer lineNumber
    String text
    Date dateCreated //TODO check if this field is added without this declaration
    String author
    static belongsTo = [projectFile: ProjectFile]

    LineComment(Integer lineNumber, String text, String author) {
        this.lineNumber = lineNumber
        this.text = text
        this.author = author

    }

    static constraints = {
        text maxSize: 4096
    }
}
