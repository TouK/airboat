package codereview

class LineComment {
    Integer lineNumber
    String text
    Date dateCreated

    static belongsTo = [projectFile: ProjectFile]

    LineComment(Integer lineNumber, String text) {
        this.lineNumber = lineNumber
        this.text = text


    }
    static constraints = {
    }
}
