package codereview

//FIXME add constraints tests
class Project {

    String name
    String url

    Project(String name, String url) {
        this.name = name
        this.url = url
    }

    static hasMany = [changesets: Changeset]

    static constraints = {
        name unique: true
        url unique: true
    }
}
