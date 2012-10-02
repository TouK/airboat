package airboat

class ThreadPositionInFile {

    Integer lineNumber

    def belongsTo = [projectFileInChangeset: ProjectFileInChangeset, thread: CommentThread]

    static constraints = {
        lineNumber nullable: true
        projectFileInChangeset unique: ['thread']
    }

    ThreadPositionInFile(ProjectFileInChangeset projectFileInChangeset, CommentThread thread, int lineNumber) {
        projectFileInChangeset.addToCommentThreadsPositions(this)
        this.thread = thread
        this.lineNumber = lineNumber
    }
}
