package codereview

import grails.buildtestdata.mixin.Build
import spock.lang.Specification
import org.joda.time.DateTime

@Build([ResetPasswordEntry])
class ResetPasswordEntriesDeleteJobSpec extends Specification {


    void "should delete all the dates older then number of hours defined by constant"() {

        given:
        def entries = (1..10).collect{ResetPasswordEntry.build()}
        entries.collect {it.dateCreated = DateTime.now().minusHours(Constants.HOURS_OF_VALID_RESET_PASSWORD_TOKEN +1).toDate(); it.save()}

        def job = new ResetPasswordEntriesDeleteJob()

        when:
        job.execute()

        then:
        ResetPasswordEntry.count() == 0
    }

    void "should not delete dates newer then number of hours defined by constant"() {

        given:
        def entries = (1..10).collect{ResetPasswordEntry.build()}
        entries.collect {it.dateCreated = DateTime.now().minusHours(Constants.HOURS_OF_VALID_RESET_PASSWORD_TOKEN -1).toDate(); it.save()}

        def job = new ResetPasswordEntriesDeleteJob()

        when:
        job.execute()

        then:
        ResetPasswordEntry.count() == 10
    }
}
