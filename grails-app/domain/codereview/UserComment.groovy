package codereview


//FIXME add constructor with all fields but dateCreated, remove usages of map-constructor
class UserComment {
    String text
    Date dateCreated
    String author

    static belongsTo = [changeset: Changeset]

    UserComment(String author, String text) {
        this.author = author
        this.text = text
    }

    static constraints = {
        author nullable: false, blank: false
        text nullable: false, blank: false
    }
}
