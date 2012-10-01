package learning

import codereview.Changeset
import grails.buildtestdata.mixin.Build
import spock.lang.Specification

@Build([Changeset])
class ElementsGroupingAndSorting extends Specification {

    def 'should sort elements'() {
        given:
        def elements = [[lineNumber:1, secondaryNumber: 2, id: 1], [lineNumber: 1, secondaryNumber: 1, id: 2], [lineNumber: 2, secondaryNumber: 1, id: 3]]

        when:
        def sorted = elements.sort{a, b -> if (a.lineNumber == b.lineNumber) a.secondaryNumber <=> b.secondaryNumber else a.lineNumber <=> b.lineNumber}

        then:
        sorted.collect{it.id} == [2, 1, 3]
    }

    def 'should group elements into list'() {
        given:
        def elements = [[lineNumber:1, secondaryNumber: 2, id: 1], [lineNumber: 1, secondaryNumber: 1, id: 2], [lineNumber: 2, secondaryNumber: 1, id: 3]]

        when:
        def grouped = elements.groupBy{it.lineNumber}.collect{key, value -> [lineNumber: key, threads: value]}

        then:
        grouped.size() == 2
    }
}
