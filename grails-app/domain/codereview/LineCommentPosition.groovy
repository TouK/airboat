package codereview

class LineCommentPosition {

    Integer lineNumber

    def belongsTo = [changeset: Changeset, projectFile: ProjectFile, comment: LineComment]

    static constraints = {
        lineNumber nullable: true
        comment unique: ['changeset']
    }

    LineCommentPosition(ProjectFile projectFile, LineComment comment, int lineNumber) {
        this.projectFile = projectFile
        this.comment = comment
        this.lineNumber = lineNumber
    }
}
