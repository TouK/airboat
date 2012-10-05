package airboat

class SnippetWithCommentsService {

    def scmAccessService

    def addSnippetsToThreadPositions(def threadPositionsWithGroupedThreads, ProjectFileInChangeset projectFileInChangeset) {
        if (threadPositionsWithGroupedThreads.isEmpty()) {
            return positions
        } else {
            def fileContent = scmAccessService.getFileContent(projectFileInChangeset.changeset, projectFileInChangeset.projectFile)
            def fileLines = fileContent.readLines()
            def threadGroupsCount = threadPositionsWithGroupedThreads.size()
            for (int i = 0; i < threadGroupsCount - 1; i++) {
                def currentLine = threadPositionsWithGroupedThreads[i].lineNumber
                def nextLine = threadPositionsWithGroupedThreads[i + 1].lineNumber
                def snippetLength = getSnippetLength(currentLine, nextLine)
                threadPositionsWithGroupedThreads[i].snippet = getSnippet(fileLines, currentLine, snippetLength)
            }
            def lastSnippetLine = threadPositionsWithGroupedThreads.last().lineNumber
            def lastSnippet = getSnippet(fileLines, lastSnippetLine, Constants.SNIPPET_LENGTH)
            threadPositionsWithGroupedThreads[threadGroupsCount - 1].snippet = lastSnippet

            return threadPositionsWithGroupedThreads
        }
    }

    private int getSnippetLength(currentLine, nextLine) {
        nextLine - Constants.SNIPPET_LENGTH >= currentLine ? Constants.SNIPPET_LENGTH : nextLine - currentLine
    }

    def getSnippet(fileLines, lineNumber, length) {
        def snippetStart = lineNumber - 1
        def snippetEnd = Math.min(snippetStart + length - 1, fileLines.size() - 1)
        fileLines[snippetStart..snippetEnd].collect {it == '' ? ' ' : it}.join('\n')
    }
}
