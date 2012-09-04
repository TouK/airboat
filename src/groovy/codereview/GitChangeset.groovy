package codereview

class GitChangeset {
    String fullMessage
    String authorEmail
    String rev
    Date date
    List<GitChangedFile> files

    GitChangeset(String fullMessage, String authorEmail, String rev, Date date) {
        this.fullMessage = fullMessage
        this.authorEmail = authorEmail
        this.rev = rev
        this.date = date
    }

}
