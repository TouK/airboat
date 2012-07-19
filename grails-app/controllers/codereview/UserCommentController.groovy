package codereview

class UserCommentController {

    def index() { }
    def addComment = {
        def names = request.getParameterNames()
        String together = ""
        names.each {
           together += it.value.toString()
        }
        render together
    }
    def receiveJSON = {
        def json = request.JSON;
        render json
    }
}
