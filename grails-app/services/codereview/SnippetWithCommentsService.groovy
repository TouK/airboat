package codereview

//FIXME rewrite this thing...
class SnippetWithCommentsService {

    def prepareThreadGroupsWithSnippets(threads, fileContent) {
        def threadGroupsWithSnippets = threads.groupBy{it.lineNumber}
        threadGroupsWithSnippets = threadGroupsWithSnippets.collect{key, value -> [lineNumber: key, threads: value.sort{it.comments[0].date}]}.sort{it.lineNumber}
        def threadGroupNumber = threadGroupsWithSnippets.size()
        def fileLines = fileContent.readLines()

        for (int i = 0; i < threadGroupNumber - 1; i++) {
            def currentLine = threadGroupsWithSnippets[i].lineNumber
            def nextLine = threadGroupsWithSnippets[i+1].lineNumber
            def snippetLength = nextLine-Constants.SNIPPET_LENGTH >= currentLine ? Constants.SNIPPET_LENGTH : nextLine-currentLine
            threadGroupsWithSnippets[i].snippet = getSnippet(fileLines, currentLine, snippetLength)
        }
        threadGroupsWithSnippets[threadGroupNumber-1].snippet = getSnippet(fileLines, threadGroupsWithSnippets[threadGroupNumber-1].lineNumber, Constants.SNIPPET_LENGTH)

        return threadGroupsWithSnippets
    }

    def getSnippet(fileLines, lineNumber, length) {
        def snippetStart = lineNumber-1
        def snippetEnd = Math.min(lineNumber+length-1-1, fileLines.size()-1)
        fileLines[snippetStart..snippetEnd].collect{it == ''? ' ' : it}.join('\n')
    }
}
