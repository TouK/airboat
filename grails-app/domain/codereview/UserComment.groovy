package codereview

class UserComment {
    String content
    Date dateCreated
    String author
    static belongsTo = [changeset: Changeset]
    static constraints = {
    }
}
