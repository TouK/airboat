package codereview

class Commiter {

    String cvsCommiterId

    Commiter(String cvsCommiterId) {
        this.cvsCommiterId = cvsCommiterId
    }

    User user

    static hasMany = [changesets: Changeset]

    static constraints = {
        cvsCommiterId blank: false, unique:true
        user nullable: true
    }
}
