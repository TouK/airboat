package codereview

/**
 * Created with IntelliJ IDEA.
 * User: touk
 * Date: 17.08.12
 * Time: 09:12
 * To change this template use File | Settings | File Templates.
 */
class GitChangeset {
    String fullMessage
    String authorEmail
    String rev
    Date date
    def files

    GitChangeset(String fullMessage, String authorEmail, String rev, Date date) {
        this.fullMessage = fullMessage
        this.authorEmail = authorEmail
        this.rev = rev
        this.date = date
    }

}
