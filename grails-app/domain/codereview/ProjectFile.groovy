package codereview

class ProjectFile {

    String name

    static belongsTo = [changeset: Changeset]
    static hasMany = [lineComments: LineComment]

    ProjectFile(String name) {
        this.name = name
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
