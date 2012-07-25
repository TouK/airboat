package codereview

class ProjectFile {

    String content
    String name

    static belongsTo = [changeset: Changeset]

    String getFileType() {
        //Temporary
        return "Groovy"
    }
    static constraints = {
        content nullable: true
        content blank: true
    }
}
