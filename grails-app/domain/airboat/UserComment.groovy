package airboat


class UserComment {
    String text
    Date dateCreated

    static belongsTo = [author: User, changeset: Changeset]

    UserComment(User author, String text) {
        author.addToChangesetComments(this)
        this.text = text
    }

    static constraints = {
        text nullable: false, blank: false, maxSize: 4096
    }
}
