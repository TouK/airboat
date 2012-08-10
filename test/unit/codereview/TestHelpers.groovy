package codereview

class TestHelpers {
    static String nLinesOfSampleText(Map parameters) {
        (1..parameters.n).collect { "line ${it}" }.join('\n')
    }
}
