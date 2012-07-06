package codereview

class Changeset {

    String identifier
    String author
    Date date

    Changeset(String identifier, String author, Date date) {
        this.identifier = identifier
        this.author = author
        this.date = date
    }

    static constraints = {
        author blank: false
        identifier blank: false, unique: true
    }
}
