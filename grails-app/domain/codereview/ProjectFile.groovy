package codereview


//FIXME project files must have a notion of file type, which is needed e.g. when showing files in changeset (think: images)
class ProjectFile {

    String content
    String name

    static belongsTo = [changeset: Changeset]
    static transients = ['content']

   ProjectFile(String name, String content) { //TODO check if content parameter is applicable here
        this.name = name
        this.content = content
    }

    static constraints = {
        content nullable: true, blank: true
        name nullable: true
    }
}
