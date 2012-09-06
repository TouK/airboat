package learning

import spock.lang.Ignore
import spock.lang.Specification
import codereview.User
import codereview.Commiter
import grails.buildtestdata.mixin.Build
import codereview.UserComment
import codereview.UnitTestBuilders
import codereview.Changeset
import java.util.Date

@Build([Changeset])
class ElementsGrouping extends Specification {

    def 'How to group elements'() {

        given:
        def elements = [Changeset.build(date: Date.parse("DD-MM-YYYY HH:mm", "12-12-2012 12:10")), Changeset.build(date: Date.parse("DD-MM-YYYY HH:mm", "12-12-2012 12:11"))]

        when:
        def grouped = elements.groupBy {it.date.date}

        then:
        grouped.size() == 1

    }
}
