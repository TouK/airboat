package codereview

class ProjectFileInChangeset {
    ChangeType changeType

    static belongsTo = [changeset: Changeset, projectFile: ProjectFile]
    static hasMany = [commentThreadsPositions: ThreadPositionInFile]

    static constraints = {
        changeset unique: ['projectFile', 'changeType'], validator: projectFileAndChangesetMustBeOfSameProject
    }

    ProjectFileInChangeset(Changeset changeset, ProjectFile projectFile, ChangeType changeType) {
        changeset.addToProjectFilesInChangeset(this)
        projectFile.addToProjectFileInChangesets(this)
        this.changeType = changeType
    }

    static def projectFileAndChangesetMustBeOfSameProject = { Changeset changeset, ProjectFileInChangeset that ->
        def projectFile = that.projectFile
        if (changeset != null && projectFile != null && changeset.project != projectFile.project) {
            ['changesetsProjectFilesMustBeOfSameProject', changeset.project.name, projectFile, projectFile.project.name]
        }
    }
}
