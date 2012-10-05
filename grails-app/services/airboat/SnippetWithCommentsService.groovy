package airboat

class SnippetWithCommentsService {

    def prepareThreadPositionsWithSnippets(positions, fileContent) {
        if (positions.isEmpty()) {
            return positions
        } else {
            def threadPositionsByLine = positions.groupBy { it.lineNumber }
            def threadPositionsByLineSorted = threadPositionsByLine.sort()
            def threadPositionsWithGroupedThreads = threadPositionsByLineSorted.collect { key, positionsForLine ->
                def threadsForLine = positionsForLine*.thread
                threadsForLine.sort { it.creationDate }
                [lineNumber: key, threads: threadsForLine]
            }
            def threadGroupsCount = threadPositionsWithGroupedThreads.size()
            def fileLines = fileContent.readLines()

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
