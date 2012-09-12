package codereview

import org.joda.time.DateTime

class ResetPasswordEntriesDeleteJob {

    def concurrent = false

    private static final long REPEAT_INTERVAL_MILLISECONDS = 1 * 60 * 60 * 1000L // once every 1h

    static triggers = {
        simple repeatInterval: REPEAT_INTERVAL_MILLISECONDS
    }

    def execute() {
        DateTime yesterday = DateTime.now().minusHours(Constants.HOURS_OF_VALID_RESET_PASSWORD_TOKEN)
        for (ResetPasswordEntry entry : ResetPasswordEntry.findAllByDateCreatedLessThan(yesterday.toDate())) {
            entry.delete()
        }
    }
}
