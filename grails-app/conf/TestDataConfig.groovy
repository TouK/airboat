testDataConfig {
    sampleData {
        'codereview.Project' {
            def (nameCounter, urlCounter) = [1, 1]
            name = { -> "name_${nameCounter++}" }
            url = { -> "url_${urlCounter++}" }
        }

        'codereview.Changeset' {
            def i = 1
            identifier = { -> "identifier_${i++}" }
        }
    }
}