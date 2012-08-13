package codereview

import static com.google.common.base.Preconditions.checkArgument

class SnippetWithCommentsService {

    def prepareCommentGroups(comments) {
        def commentGroups = []
        def i = 0
        def j = 0
        def commentsSortedByLineNumber = comments.sort { it.lineNumber }
        def lastLineNumber = comments[0].lineNumber
        commentsSortedByLineNumber.each {                              //group comments if they're talking about same line
            if (it.lineNumber == lastLineNumber) {
                if (commentGroups[i] == null) {
                    commentGroups[i] = []
                }
                commentGroups[i][j++] = it

            }
            else {
                lastLineNumber = it.lineNumber
                i++
                commentGroups[i] = []
                j = 0
                commentGroups[i][j++] = it
            }
        }
        return commentGroups
    }

    def prepareCommentGroupsWithSnippets(commentGroups, String fileType, fileContent) {
        def commentGroupsWithSnippets = []
        def i = 0
        def snippet
        while (i < commentGroups.size() - 1) {      //how long snippet do we need?

            if (commentGroups[i + 1][0].lineNumber - commentGroups[i][0].lineNumber == 1) {
                snippet = getSnippet(commentGroups[i][0], 1, fileContent)
            }
            else if (commentGroups[i + 1][0].lineNumber - commentGroups[i][0].lineNumber == 2) {
                snippet = getSnippet(commentGroups[i][0], 2, fileContent)
            }
            else {
                snippet = getSnippet(commentGroups[i][0], 3, fileContent)
            }
            commentGroupsWithSnippets[i] = [commentGroup: commentGroups[i], snippet: snippet, filetype: fileType]
            i++
        }

        snippet = getSnippet(commentGroups[i][0], 3, fileContent)
        commentGroupsWithSnippets[i] = [commentGroup: commentGroups[i], snippet: snippet, filetype: fileType]
        return commentGroupsWithSnippets
    }

    def getSnippet(comment, howManyLines, fileContent) {
        return getLinesAround(fileContent, comment.lineNumber, howManyLines)
    }

    def getLinesAround(String text, Integer from, Integer count) {
        checkArgument(from > 0, 'from must be a positive integer' as Object) //TODO introduce own defensive programming helpers not needing the "as Object" cast
        checkArgument(count > 0, 'count must be a positive integer' as Object)
        def splitted = text.split('\n')
        def fromZeroBasedInclusive = from - 1
        checkArgument(fromZeroBasedInclusive < splitted.size(), "from (=${from}) must be a valid line number in text (which has ${splitted.size()} lines).")
        def toZeroBasedExclusive = Math.min(fromZeroBasedInclusive + count, splitted.size())

        return splitted[fromZeroBasedInclusive..toZeroBasedExclusive - 1].join('\n')
    }
}
