package codereview

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

    def getLinesAround(String text, Integer at, Integer howMany) {
        def splitted = text.split("\n")
        def from = at
        def to = at + howMany - 1
        if (from < 0) {
            return null

        }
        if (to >= splitted.size()) {
            if (at < splitted.size()) {
                to = at
            }
            else {
                return null
            }
        }
        return splitted[from.toInteger()..to.toInteger()].join("\n")
    }
}
