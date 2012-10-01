package codereview

//FIXME add constraints tests
class Project {

    enum ProjectState {
        notImported, triedToBeInitiallyImported, initiallyImported, fullyImported
    }

    String name //FIXME get rid of this field, since we assume in code all around that it can be computed from url
    String url
    ProjectState state = Project.ProjectState.notImported

    Project(String name, String url) {
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
