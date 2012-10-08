package airboat

class UserComment {

    String text
    Date dateCreated
    boolean isArchived

    static belongsTo = [author: User, changeset: Changeset]

    static constraints = {
        text blank: false, maxSize: 4096
    }

    UserComment(User author, String text) {
        author.addToChangesetComments(this)
        isArchived = false
        this.text = text
    }
}
