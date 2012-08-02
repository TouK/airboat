package codereview


import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON
import spock.lang.Ignore

@TestFor(UserCommentController)
@Mock([Commiter, Changeset, UserComment, Project])
class UserCommentControllerSpec extends Specification {

    def "should return comments to changeset when given right changeset id"() {
        given:
        def changeset = new Changeset("hash23", "zmiany", new Date()).addToUserComments(
                new UserComment(text: "fajno", author: "jil@touk.pl")
        )
        new Commiter('agj').addToChangesets(changeset).save()
        new Project("testProject", "testUrl").addToChangesets(changeset).save()

        when:
        params.id = "hash23"
        controller.returnCommentsToChangeset()

        then:
        response.json.size() == 1
        response.json[0].text == "fajno"
    }

    def "should return last comments, sorted by dateCreated, descending"() {
        given:
        def project = new Project("testProject", "testUrl") 
        def commiter = new Commiter("agj")
        def changeset = new Changeset("hash23", "zmiany", new Date())
        def comments = [
                new UserComment("Jil", "oooohhhh"),
                new UserComment("Troll", "oooohhhh?"),
                new UserComment("Jil", "No way!")
        ]

        project.addToChangesets(changeset)
        commiter.addToChangesets(changeset)
        comments.each() {
            changeset.addToUserComments(it)
        }
        
        project.save()
        changeset.save()

        when:
        controller.getLastComments()
        
        then:
        String rendered = response.json
        rendered.size() == comments.size()
        rendered[0].toString().contains('"id":3,"author":"Jil"')
        rendered[1].toString().contains('"id":2,"author":"Troll"')
        rendered[2].toString().contains('"id":1,"author":"Jil"')
    }


    def "should add comment correctly to db"() {
        given:
        def project = new Project("testProject", "testUrl")
        def commiter = new Commiter("agj")
        def changesetId = "hash23"
        def changeset = new Changeset(changesetId, "zmiany", new Date())
        def username = "Jil"
        def text = "oooohhhh"
        def comments = [
                new UserComment(username, text),
                new UserComment("Troll", "oooohhhh?"),
                new UserComment("Jil", "No way!")
        ]

        project.addToChangesets(changeset)
        commiter.addToChangesets(changeset)
        comments.each() {
            changeset.addToUserComments(it)
        }

        project.save()
        commiter.save()

        when:
        controller.addComment(username, text, changesetId)

        then:
        UserComment.findByText(text) != null
        UserComment.findByTextAndAuthor(text, username) != null
        UserComment.findByChangeset(changeset) != null
    }


}