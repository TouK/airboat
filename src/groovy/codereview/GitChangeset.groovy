package codereview

class GitChangeset {
    String fullMessage
    String gitCommitterId
    String rev
    Date date
    List<GitChangedFile> files

    GitChangeset(String fullMessage, String gitCommitterId, String rev, Date date) {
        this.fullMessage = fullMessage
        this.gitCommitterId = gitCommitterId
        this.rev = rev
        this.date = date
    }
}
