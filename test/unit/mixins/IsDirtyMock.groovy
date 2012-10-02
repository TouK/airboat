package mixins

import airboat.User

@Category(User) class IsDirtyMock {

    boolean isDirty(String fieldName) {
        false
    }
}
