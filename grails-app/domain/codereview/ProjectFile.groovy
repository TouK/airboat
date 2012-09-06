package codereview

class ProjectFile {

    String name
    Project project

    static belongsTo = [Project]
    static hasMany = [projectFileInChangesets: ProjectFileInChangeset]

    ProjectFile(String name) {
        this.name = name
    }

    String getFileType() {
        def extensionToFiletype = [js: 'javascript', htm: 'html']
        extensionToFiletype.get(extension, extension)
    }

    private String getExtension() {
        def extension
        if (name.contains('.')) {
            extension = name[name.lastIndexOf('.') + 1..name.length() - 1]
        } else {
            extension = ""
        }
        extension
    }

    static constraints = {
        name blank: false
        projectFileInChangesets minSize: 1
    }
}
