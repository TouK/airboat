package codereview

class ProjectFile {
    static def textFileFormats = [
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
            "xml",
            "javascript",
            "json",
            "sh"
    ]

    String name
    ChangeType changeType

    static belongsTo = [changeset: Changeset]
    static hasMany = [lineComments: LineComment]

    ProjectFile(String name, ChangeType changeType) {
        this.name = name
        this.changeType = changeType
    }

    String getFileType() {
        def extensionToFileType = [js: 'javascript', htm: 'html']
        def extension = getExtension()
        extensionToFileType.get(extension, extension)
    }

    private String getExtension() {
        def extension
        if(name.contains('.')) {
            extension = name[name.lastIndexOf('.') + 1..name.length() - 1]
        }
        else  {
            extension = ""
        }
        extension
    }

    boolean isTextFormat() {
        textFileFormats.contains(fileType)
    }

    static constraints = {
        name blank: false
    }
}
