package codereview

class Commiter {

    String cvsCommiterId

    Commiter(String cvsCommiterId) {
        this.cvsCommiterId = cvsCommiterId
    }

    //TODO this belongs to User class, changesets should get their email by getting Changeset.committer.user.email
    String getEmail() {
        if (cvsCommiterId.contains("@")) {
            return cvsCommiterId[cvsCommiterId.indexOf("<") + 1 .. cvsCommiterId.indexOf(">") - 1]
        } else {
            return null;
        }
    }

    static hasMany = [changesets: Changeset]
    static constraints = {
        cvsCommiterId blank: false
    }
}
