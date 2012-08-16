package mixins

import codereview.User

@Category(User) class IsDirtyMock {

    boolean isDirty(String fieldName) {
        false
    }
}
