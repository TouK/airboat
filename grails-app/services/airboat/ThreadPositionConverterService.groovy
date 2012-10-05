package airboat

class ThreadPositionConverterService {

    def commentConverterService

    List<Map<String, Object>> getThreadPositionsProperties(ProjectFileInChangeset projectFileInChangeset) {
        def positionsForLinesSorted = projectFileInChangeset.commentThreadsPositions
                .groupBy { it.lineNumber }
                .sort()

        def threadPositions = positionsForLinesSorted.collect() { lineNumber, positions ->
            [
                    lineNumber: lineNumber,
                    threads: positions
                            .collect { position -> getThreadProperties(position.thread) }
                            .sort { it.comments.first().dateCreated }
            ]
        }
        threadPositions
    }

    private def getThreadProperties(CommentThread commentThread) {
        [
                id: commentThread.id,
                comments: getCommentsProperties(commentThread).sort { it.dateCreated }
        ]
    }

    private List<Map<String, String>> getCommentsProperties(CommentThread commentThread) {
        commentThread.comments.collect commentConverterService.&getCommentJSONproperties
    }
}
