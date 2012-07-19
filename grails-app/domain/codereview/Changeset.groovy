package codereview

class Changeset {

    String identifier
    String author
    String commitComment
    Date date

    static hasMany = [projectFiles: ProjectFile, userComments: UserComment]


    Changeset(String identifier, String author, Date date) {      //TODO change tests to use constructor below and remove this one
        this.identifier = identifier
        this.author = author
        this.date = date
    }

    Changeset(String identifier, String author, String commitComment, Date date) {
        this.identifier = identifier
        this.author = author
        this.date = date
        this.commitComment = commitComment
    }

    String getEmail() {
      return author[author.indexOf("<")+1.. author.indexOf(">")-1]
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
