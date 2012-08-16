package mixins

import codereview.User

@Category(User) class IsDirtyMock {

    boolean dirty

    boolean isDirty(String fieldName) {
        dirty
    }
}
