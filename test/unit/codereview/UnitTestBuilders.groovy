package codereview

import mixins.IsDirtyMock

class UnitTestBuilders {
    static User buildUserWithIsDirtyMock(Map parameters) {
        User user
        if (parameters?.username == null) {
            user = User.buildWithoutSave()
        } else {
            user = User.buildWithoutSave(username: parameters.username)
        }
        user.metaClass.mixin(IsDirtyMock)
        user.save(failOnError: true)
    }
}
