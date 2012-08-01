package codereview

class Commiter {

    String cvsCommiterId

    Commiter(String cvsCommiterId) {
        this.cvsCommiterId = cvsCommiterId
    }

    static hasMany = [changesets: Changeset]
    static constraints = {
        cvsCommiterId blank: false
    }
}
