package airboat

class ProjectFile {
    String name
    Project project

    static belongsTo = [Project]
    static hasMany = [projectFileInChangesets: ProjectFileInChangeset]

    static constraints = {
        name blank: false
    }
    
    ProjectFile(String name) {
        this.name = name
    }

    boolean isTextFormat() {
        textFileFormats.contains(fileType)
    }

    private static def textFileFormats = [
            "java",
            "groovy",
            "html",
            "htm",
            "jsp",
            "gsp",
            "py",
            "rb",
            "h",
            "c",
            "cpp",
            "txt",
            "md",
            "php",
            "",
            "css",
            "less",
            "xml",
            "javascript",
            "json",
            "sh",
            "properties"
    ]
    
    String getFileType() {
        extensionToFiletype.get(extension, extension)
    }

    private static def extensionToFiletype = [js: 'javascript', htm: 'html']

    private String getExtension() {
        def extension
        if (name.contains('.')) {
            extension = name[name.lastIndexOf('.') + 1..name.length() - 1]
        } else {
            extension = ""
        }
        extension
    }
    
    
}
