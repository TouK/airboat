import codereview.Changeset

class BootStrap {

    def init = { servletContext ->
        new Changeset("hash23", "agj", new Date()).save()
        new Changeset("hash24", "kpt", new Date()).save()
    }
    def destroy = {
    }
}
