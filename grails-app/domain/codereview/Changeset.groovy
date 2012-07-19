package codereview

class Changeset {

    String identifier
    String author
    Date date

    static hasMany = [projectFiles: ProjectFile]

    Changeset(String identifier, String author, Date date) {
        this.identifier = identifier
        this.author = author
        this.date = date
    }


    String getEmail() {
      return author[author.indexOf("<")+1.. author.indexOf(">")-1]
    }

    static constraints = {
        author blank: false
        identifier blank: false, unique: true
        email nullable: true, blank:true
        projectFiles nullable: true
    }

}
