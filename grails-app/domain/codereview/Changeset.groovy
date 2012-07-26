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

     //TODO Later on should be called on User object. Make sure user object stores EMAIL and not git user string
    String getEmail() {
        if (author?.contains("@")) {
            return author[author.indexOf("<") + 1 .. author.indexOf(">") - 1]
        } else {
            return null;
        }
    }

    Integer commentsCount() {
        return UserComment.findAllByChangeset(this).size()
    }

    static constraints = {
        author blank: false
        identifier blank: false, unique: true
        email nullable: true, blank:true
        commitComment blank: true
    }
}
