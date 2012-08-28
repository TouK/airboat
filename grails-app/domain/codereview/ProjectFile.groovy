package codereview

class ProjectFile {

    String name
    ChangeType changeType
    Project project

    static belongsTo = [Changeset, Project]
    static hasMany = [changesets: Changeset]

    ProjectFile(String name, ChangeType changeType) {
        this.name = name
        this.changeType = changeType
    }

    String getFileType() {
        def extensionToFiletype = [js: 'javascript', htm: 'html']
        def extension = name[name.lastIndexOf('.') + 1..name.length() - 1]
        extensionToFiletype.get(extension, extension)
    }

    static constraints = {
        name blank: false
    }
}
