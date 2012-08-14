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
         commentGroups
    }

    def prepareCommentGroupsWithSnippets(commentGroups, String fileType, fileContent) {
        def commentGroupsWithSnippets = []
        def iterator = 0
        def snippet
        def oneLine = 1
        def twoLines = 2
        def threeLines = 3
        while (iterator < commentGroups.size() - 1) {      //how long snippet do we need?

            if (commentGroups[iterator + 1][0].lineNumber - commentGroups[iterator][0].lineNumber == 1) {
                snippet = getSnippet(commentGroups[iterator][0], oneLine, fileContent)
            }
            else if (commentGroups[iterator + 1][0].lineNumber - commentGroups[iterator][0].lineNumber == 2) {
                snippet = getSnippet(commentGroups[iterator][0], twoLines, fileContent)
            }
            else {
                snippet = getSnippet(commentGroups[iterator][0], threeLines, fileContent)
            }
            commentGroupsWithSnippets[iterator] = [commentGroup: commentGroups[iterator], snippet: snippet, filetype: fileType]
            iterator++
        }

        snippet = getSnippet(commentGroups[iterator][0], threeLines, fileContent)
        commentGroupsWithSnippets[iterator] = [commentGroup: commentGroups[iterator], snippet: snippet, filetype: fileType]
         commentGroupsWithSnippets
    }

    def getSnippet(comment, howManyLines, fileContent) {
         getLinesAround(fileContent, comment.lineNumber, howManyLines)
    }

    def getLinesAround(String text, Integer from, Integer count) {
        checkArgument(from > 0, 'from must be a positive integer' as Object) //TODO introduce own defensive programming helpers not needing the "as Object" cast
        checkArgument(count > 0, 'count must be a positive integer' as Object)
        def splitted = text.split('\n')
        def fromZeroBasedInclusive = from - 1
        checkArgument(fromZeroBasedInclusive < splitted.size(), "from (=${from}) must be a valid line number in text (which has ${splitted.size()} lines).")
        def toZeroBasedExclusive = Math.min(fromZeroBasedInclusive + count, splitted.size())

         splitted[fromZeroBasedInclusive..toZeroBasedExclusive - 1].join('\n')
    }
}
