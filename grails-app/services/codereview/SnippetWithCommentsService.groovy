package codereview

import static com.google.common.base.Preconditions.checkArgument

//FIXME rewrite this thing...
class SnippetWithCommentsService {

    def prepareCommentGroups(comments) {
        def commentGroups = []
        def iteratorOne = 0
        def iteratorTwo = 0
        def commentsSortedByLineNumber = comments.sort { it.lineNumber }
        def lastLineNumber = comments[0].lineNumber
        commentsSortedByLineNumber.each {                              //group comments if they're talking about same line
            if (it.lineNumber == lastLineNumber) {
                if (commentGroups[iteratorOne] == null) {
                    commentGroups[iteratorOne] = []
                }
                commentGroups[iteratorOne][iteratorTwo++] = it

            }
            else {
                lastLineNumber = it.lineNumber
                iteratorOne++
                commentGroups[iteratorOne] = []
                iteratorTwo = 0
                commentGroups[iteratorOne][iteratorTwo++] = it
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
        ArrayList sortedCommentGroups = sortCommentGroupByDateCreated(commentGroups)
        while (iterator < sortedCommentGroups.size() - 1) {      //how long snippet do we need?
            def currentGroup = sortedCommentGroups[iterator][0]
            def nextGroup = sortedCommentGroups[iterator + 1]
            if (nextGroup[0].lineNumber - currentGroup.lineNumber == 1) {
                snippet = getSnippet(currentGroup, oneLine, fileContent)
            }
            else if (nextGroup[0].lineNumber - currentGroup.lineNumber == 2) {
                snippet = getSnippet(currentGroup, twoLines, fileContent)
            }
            else {
                snippet = getSnippet(currentGroup, threeLines, fileContent)
            }
            commentGroupsWithSnippets[iterator] = [commentGroup: sortedCommentGroups[iterator], snippet: snippet, fileType: fileType]
            iterator++
        }

        snippet = getSnippet(sortedCommentGroups[iterator][0], threeLines, fileContent)
        commentGroupsWithSnippets[iterator] = [commentGroup: sortedCommentGroups[iterator], snippet: snippet, fileType: fileType]
        commentGroupsWithSnippets
    }

    private ArrayList sortCommentGroupByDateCreated(commentGroups) {
        commentGroups*.sort { it.dateCreated }
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
