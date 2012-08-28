package codereview

class Changeset {

    String identifier
    String commitComment
    Date date

    static hasMany = [projectFiles: ProjectFile, userComments: UserComment, lineCommentsPositions: LineCommentPosition]
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
        identifier blank: false, unique: true
        commitComment blank: true, maxSize: 4096
        projectFiles validator: { Set<ProjectFile> files, Changeset that ->
            def offendingFiles = files.findAll { it.project != that.project }
            if (!offendingFiles.isEmpty()) {
                def offendingFilesAndTheirProjectsNames = [offendingFiles*.name, offendingFiles*.project*.name].transpose()
                ['changesetsProjectFilesMustBeInSameProject', that.project.name, offendingFilesAndTheirProjectsNames]
            }
        }
    }
}
