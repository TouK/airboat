package codereview

//FIXME add constraints tests
class Project {

    String name //FIXME get rid of this field, since we assume in code all around that it can be computed from url
    String url
    boolean wasOnceFullyImported

    Project(String name, String url) {
        this.wasOnceFullyImported = false;
        this.name = name
        this.url = url
    }

    static hasMany = [changesets: Changeset]

    static constraints = {
        name unique: true
        name blank: false
        name size: 2..80
        url blank:  false
        url unique: true
        //url url: true //FIXME restore this constraint after extending it with allowance for file:// protocol (in test environment only, use http://grails.org/plugin/constraints)
    }

    boolean hasChangesets() {
        changesets && !changesets.isEmpty()
    }
}
