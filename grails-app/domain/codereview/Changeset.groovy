package codereview

class Changeset {

    String identifier
    String commitMessage
    Date date

    static hasMany = [projectFilesInChangeset: ProjectFileInChangeset, userComments: UserComment]
    static belongsTo = [project: Project, commiter: Commiter]

    static mapping = {
        userComments sort: 'dateCreated', order: 'asc'
        sort 'date'
    }

    Commiter commiter

    Changeset(String identifier, String commitMessage, Date date) {
        this.identifier = identifier
        this.date = date
        this.commitMessage = commitMessage
    }

    int getCommentsCount() {
        userComments ? userComments.size() : 0
    }

    static constraints = {
        identifier blank: false, unique: ['project']
        commitMessage blank: true, maxSize: 4096
        projectFilesInChangeset minSize: 1
    }
}
