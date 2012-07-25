package codereview

class UserComment {
    String text
    Date dateCreated
    String author

    static belongsTo = [changeset: Changeset]

    static constraints = {
        author nullable: false, blank: false
        text nullable: false, blank: false
    }
}
