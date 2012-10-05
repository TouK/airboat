package airboat

class UserComment {

    String text
    Date dateCreated

    static belongsTo = [author: User, changeset: Changeset]

    static constraints = {
        text blank: false, maxSize: 4096
    }

    UserComment(User author, String text) {
        author.addToChangesetComments(this)
        this.text = text
    }
}
