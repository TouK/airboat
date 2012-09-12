package codereview

class ResetPasswordEntry {

    String token
    Date dateCreated

    static belongsTo = [user: User]

    static constraints = {
        token blank: false
        user unique:  true
    }

    def beforeInsert() {
        dateCreated = new Date()
    }
}
