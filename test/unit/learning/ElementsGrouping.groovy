package learning

import codereview.Changeset
import grails.buildtestdata.mixin.Build
import spock.lang.Specification

@Build([Changeset])
class ElementsGrouping extends Specification {

    def 'How to group elements'() {

        given:
        def elements = [Changeset.build(date: Date.parse("dd-MM-yyyy HH:mm", "12-12-2012 12:10")), Changeset.build(date: Date.parse("dd-MM-yyyy HH:mm", "12-12-2012 12:11"))]

        when:
        def grouped = elements.groupBy { def dateParam = it.date.format('yyyy-MM-dd HH:mm'); return dateParam.substring(0, 10)}

        then:
        grouped.size() == 1

    }
}
