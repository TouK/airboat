package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture
import org.apache.maven.scm.ChangeSet
import grails.plugin.spock.IntegrationSpec

//@Mock([Changeset, ProjectFile, Commiter])
class ScmAccessServiceIntegrationSpec extends IntegrationSpec {

    ScmAccessService scmAccessService

    def setup() {
        scmAccessService = new ScmAccessService()
    }

    def "should fetch and save changesets in db"() {    //TODO it's inconsistent with our naming convention
        given:
        def (gitScmUrl, changesetId, commitComment, changesetAuthor)  = [Fixture.PROJECT_REPOSITORY_URL, "id", "comment", "agj@touk.pl"]

        def cs = new ChangeSet(new Date(), commitComment, changesetAuthor, null)
        cs.setRevision(changesetId)
        GitRepositoryService gitRepositoryService = Mock()
        1 * gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL) >> [ cs ]

        scmAccessService.gitRepositoryService = gitRepositoryService

        when:
        scmAccessService.importAllChangesets(gitScmUrl)

        then:
        Changeset.count() == 1
        Changeset.findAllByIdentifierAndCommitComment(changesetId, commitComment).size() == 1
    }

    //TODO this testing is incomplete, because service has got many methods and they're aren't tested anywhere - More tests!

    //FIXME test for sort order of changesets
    def "should convert git ChangeSet to ChangeSet and add commiter" () {
        given:
        def gitChangeSet = new ChangeSet(new Date(), "Refactoring", "jil <jil@touk.pl>", null)

        when:
        def changeset = scmAccessService.convertToChangeset(gitChangeSet)
        def changesetCommiter = changeset.commiter

        then:
        changeset != null
        changesetCommiter != null

    }

    def "should save Changeset to db and add commiter if it is not yet in db" () {
        given:
        def cvsCommiterId = "jil <jil@touk.pl>"
        def gitChangeSet = new ChangeSet(new Date(), "Refactoring", cvsCommiterId, null)

        when:
        def changeset = scmAccessService.convertToChangeset(gitChangeSet)
        scmAccessService.saveChangeset(changeset)

        then:
        def commiter = Commiter.findByCvsCommiterId(cvsCommiterId)
        commiter != null
        Changeset.findByCommiter(commiter) != null
        def changesetFromDB =  Changeset.findByCommiter(changeset.commiter)
        Commiter.findByCvsCommiterId(changesetFromDB.getEmail())  == 1

    }

    def "should save Changeset to db and not add a new commiter to db if it already exists"  () {
        given:
        new Commiter("Jil <jil@touk.pl>").save()

        when:
        def changeset = new Changeset("hash23", "refactoring", new Date())
        def commiter = new Commiter("Jil <jil@touk.pl>")
        commiter.addToChangesets(changeset)
        scmAccessService.saveChangeset(changeset)
        def changesetFromDB =  Changeset.findByAuthor("jil <jil@touk.pl>")
        def commiterFromDb = Commiter.findByEmail("jil@touk.pl")
        Commiter.findByChangesets

        then:
        Changeset.findByCommiter(commiterFromDb) != null
        Commiter.findByCvsCommiterId(changesetFromDB.getEmail()) == 1
    }
}
