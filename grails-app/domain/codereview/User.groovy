package codereview

class User {

    String email
    String displayName
    String saltedPasswordHash

    static hasMany = [commiters: Commiter]

    static constraints = {
    }
}
