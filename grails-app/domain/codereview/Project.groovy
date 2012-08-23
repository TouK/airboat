package codereview

//FIXME add constraints tests
class Project {

    String name //FIXME get rid of this field, since we assume in code all around that it can be computed from url
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

    boolean hasChangesets() {
        changesets && !changesets.isEmpty()
    }
}
