package codereview

class Changeset {

    String identifier
    String author
    String commitComment
    Date date

    static hasMany = [projectFiles: ProjectFile, userComments: UserComment]

    Changeset(String identifier, String author, String commitComment, Date date) {
        this.identifier = identifier
        this.author = author
        this.date = date
        this.commitComment = commitComment
    }

    /**
     * TODO Later on should be called on User object.
     * @return
     */
    String getEmail() {
        if (author != null && author.contains("@")) {
            return author[author.indexOf("<") + 1 .. author.indexOf(">") - 1]
        } else {
            return null;
        }
    }

    static constraints = {
        author blank: false
        identifier blank: false, unique: true
        email nullable: true, blank:true
        projectFiles nullable: true
        userComments nullable: true
        commitComment nullable: true, blank: true //TODO remove it after changing tests
    }

}
