package codereview

class ProjectFile {
    String content
    String name
    static belongsTo = [changeset: Changeset]

    static constraints = {
        content nullable: true
        content blank: true
    }
}
