package codereview

class Changeset {

    String identifier
    String commitComment
    Date date

    static hasMany = [projectFiles: ProjectFile, userComments: UserComment]
    static belongsTo = [commiter: Commiter]

    Changeset(String identifier, String commitComment, Date date) {
        this.identifier = identifier
        this.date = date
        this.commitComment = commitComment
    }

    Integer commentsCount() {
        return UserComment.findAllByChangeset(this).size()
    }

    static constraints = {
        identifier blank: false, unique: true
        commitComment blank: true
    }
}
