package codereview

class ProjectFile {

    String content
    String name

    static belongsTo = [changeset: Changeset]
    static hasMany = [lineComments: LineComment]
    static transients = ['content'] //TODO this does not belong here, move it to a DTO

    ProjectFile(String name) {
        this.name = name
    }

    String getFileType() {
        def extensionToFiletype = [js: 'javascript', htm: 'html']
        def extension = name[name.lastIndexOf('.') + 1..name.length() - 1]
        extensionToFiletype.get(extension, extension)
    }

    static constraints = {
        content nullable: true, blank: true
        name blank: false
    }
}
