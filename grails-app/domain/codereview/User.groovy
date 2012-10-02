package airboat

class User {

    transient springSecurityService

    String username
    String password
    String skin = "default"
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    static hasMany = [committers: Commiter, changesetComments: UserComment, lineComments: LineComment]


    static constraints = {
        username blank: false, email: true, unique: true
        email blank: false, email: true, unique: true
        password blank: false
    }

    static mapping = {
        password column: '`password`'
    }

    User(String username, String password) {
        this.username = username
        this.password = password
    }

    String getEmail() {
        username
    }

    void setEmail(String email) {
        username = email
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }



    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }
}
