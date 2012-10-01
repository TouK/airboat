package codereview

class Commiter {

    String cvsCommiterId

    Commiter(String cvsCommiterId) {
        this.cvsCommiterId = cvsCommiterId
    }

    static belongsTo = [user: User]

    static hasMany = [changesets: Changeset]

    static constraints = {
        cvsCommiterId blank: false, unique:true
        user nullable: true
    }

    def getEmail() {
        cvsCommiterId //TODO this holds for git, not for svn
    }
}
