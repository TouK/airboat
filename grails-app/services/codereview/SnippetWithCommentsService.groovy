package codereview

import static com.google.common.base.Preconditions.checkArgument

class SnippetWithCommentsService {

    def prepareSnippetsGroupList(comments) {
        def snippetsGroup = []
        def i = 0
        def j = 0
        def commentsWithSnippets = comments.sort { it.lineNumber }
        def lastLineNumber = comments[0].lineNumber
        commentsWithSnippets.each {                              //group comments if they're talking about same line
            if (it.lineNumber == lastLineNumber) {
                if (snippetsGroup[i] == null) {
                    snippetsGroup[i] = []
                }
                snippetsGroup[i][j++] = it

            }
            else {
                lastLineNumber = it.lineNumber
                i++
                snippetsGroup[i] = []
                j = 0
                snippetsGroup[i][j++] = it
            }
        }
        return snippetsGroup
    }

    def prepareCommentGroupsWithSnippets(snippetsGroup, String fileType, fileContent) {
        def commentGroupsWithSnippets = []
        def i = 0
        def snippet
        while (i < snippetsGroup.size() - 1) {      //how long snippet do we need?

            if (snippetsGroup[i + 1][0].lineNumber - snippetsGroup[i][0].lineNumber == 1) {
                snippet = getSnippet(snippetsGroup[i][0], 1, fileContent)
            }
            else if (snippetsGroup[i + 1][0].lineNumber - snippetsGroup[i][0].lineNumber == 2) {
                snippet = getSnippet(snippetsGroup[i][0], 2, fileContent)
            }
            else {
                snippet = getSnippet(snippetsGroup[i][0], 3, fileContent)
            }
            commentGroupsWithSnippets[i] = [commentGroup: snippetsGroup[i], snippet: snippet, filetype: fileType]
            i++
        }

        snippet = getSnippet(snippetsGroup[i][0], 3, fileContent)
        commentGroupsWithSnippets[i] = [commentGroup: snippetsGroup[i], snippet: snippet, filetype: fileType]
        return commentGroupsWithSnippets
    }

    def getSnippet(LineComment comment, howManyLines, fileContent) {
        return getLinesAround(fileContent, comment.lineNumber, howManyLines)
    }

    def getLinesAround(String text, Integer from, Integer count) {
        checkArgument(from > 0, "from must be a positive integer" as Object) //TODO introduce own defensive programming helpers not needing the "as Object" cast
        checkArgument(count > 0, "count must be a positive integer" as Object)
        def splitted = text.split("\n")
        def fromZeroBasedInclusive = from - 1
        checkArgument(fromZeroBasedInclusive < splitted.size(), "from (=${from}) must be a valid line number in text (which has ${splitted.size()} lines).")
        def toZeroBasedExclusive = Math.min(fromZeroBasedInclusive + count, splitted.size())

        return splitted[fromZeroBasedInclusive..toZeroBasedExclusive - 1].join("\n")
    }
}
