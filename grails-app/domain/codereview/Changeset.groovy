package codereview

class Changeset {

    String identifier
    String commitComment
    Date date

    static hasMany = [projectFiles: ProjectFile, userComments: UserComment]
    static belongsTo = [
            project: Project,
            commiter: Commiter
    ]

    Changeset(String identifier, String commitComment, Date date) {
        this.identifier = identifier
        this.date = date
        this.commitComment = commitComment
    }

    int getCommentsCount() {
        userComments ? userComments.size() : 0
    }

    static constraints = {
        identifier blank: false, unique: ['project']
        commitComment blank: true, maxSize: 4096
    }
}
