package codereview

class UserComment {
    String content
    Date dateCreated
    String author

    static belongsTo = [changeset: Changeset]

    static constraints = {
        author blank: false, nullable: false
        content nullable: false, blank: false
    }
}
