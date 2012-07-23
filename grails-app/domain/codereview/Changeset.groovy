package codereview

class Changeset {

    String identifier
    String author
    String commitComment
    Date date

    static hasMany = [projectFiles: ProjectFile, userComments: UserComment]

    Changeset(String identifier, String author, String commitComment, Date date) {
        this.identifier = identifier
        this.author = author
        this.date = date
        this.commitComment = commitComment
    }

    /**
     * TODO Later on should be called on User object.
     * @return
     */
    String getEmail() {
        if (author != null && author.contains("@")) {   //TODO: refactor "@","<",">","1" on variables
            return author[author.indexOf("<") + 1 .. author.indexOf(">") - 1]
            //TODO I would suggest extracting this code in another function
            //TODO because it was only a temporary solution based on data from maven git scm api
            //TODO this should be hidden, abstraction level is incorrect
            //TODO for example String (or Email) extractEmailFromString(String stringWithEmailInIt)
            //TODO we can think about Email class, but it's not necessary as for now
        } else {
            return null;                 //TODO: method shouldn't return null?
        }

    }

    String countComments() {        //TODO implement me!
        def commentsCount
        def comments = UserComment.findAllByChangeset(this)
        commentsCount = comments.size()
        return commentsCount.toString()
    }
    static constraints = {
        author blank: false
        identifier blank: false, unique: true
        email nullable: true, blank:true
        projectFiles nullable: true
        userComments nullable: true
        commitComment nullable: true, blank: true //TODO remove it after changing tests
    }


}
