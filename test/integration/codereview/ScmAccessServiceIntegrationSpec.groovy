package codereview

import testFixture.Fixture
import org.apache.maven.scm.ChangeSet
import grails.plugin.spock.IntegrationSpec
import org.springframework.dao.InvalidDataAccessApiUsageException

class ScmAccessServiceIntegrationSpec extends IntegrationSpec {

    ScmAccessService scmAccessService

    def setup() {
        scmAccessService = new ScmAccessService()
    }

    def "should fetch and save changesets in db"() {
        given:
        def (gitScmUrl, changesetId, commitComment, changesetAuthor)  = [Fixture.PROJECT_REPOSITORY_URL, "id", "comment", "agj@touk.pl"]

        def changeSet = new ChangeSet(new Date(), commitComment, changesetAuthor, null)
        changeSet.setRevision(changesetId)
        GitRepositoryService gitRepositoryService = Mock()
        1 * gitRepositoryService.getAllChangeSets(Fixture.PROJECT_REPOSITORY_URL) >> [ changeSet ]

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
        def cvsCommitterId = "jil <jil@touk.pl>"
        def commitId = "hash23"

        when:
        def changeset = new Changeset(commitId, "refactoring", new Date())
        def committer = new Commiter(cvsCommitterId)
        committer.addToChangesets(changeset)
        scmAccessService.saveChangeset(changeset)

        then:
        Commiter.count() == 1
        def committerFromDb = Commiter.findByCvsCommiterId(cvsCommitterId)
        committerFromDb.changesets.size() == 1
        committerFromDb.changesets.contains(Changeset.findByIdentifier(commitId))
    }

    def "should add a changeset for existing committer when saving changeset by them"() {
        given:
        def cvsCommitterId = "Jil <jil@touk.pl>"
        def previouslySavedCommitter = new Commiter(cvsCommitterId)
        previouslySavedCommitter.addToChangesets(new Changeset("hash24", "first!", new Date()))
        previouslySavedCommitter.save()
        def commitId = "hash23"

        when:
        def changeset = new Changeset(commitId, "refactoring", new Date())
        def committer = new Commiter(cvsCommitterId)
        committer.addToChangesets(changeset)
        scmAccessService.saveChangeset(changeset)

        then:
        Commiter.count() == 1
        def committerFromDb = Commiter.findByCvsCommiterId(cvsCommitterId)
        committerFromDb.id == previouslySavedCommitter.id
        committerFromDb.changesets.size() == 2
        committerFromDb.changesets.contains(Changeset.findByIdentifier(commitId))
    }

    //learning tests. TODO move to another class
    def "should not validate if subobjects don't validate"() {
        when:
        Commiter committer = new Commiter("valid id").addToChangesets(new Changeset(null, null, null))

        then:
        committer.validate() == false
    }

    def "should throw an exception when using a transient association search key"() {
        when:
        Changeset.findByCommiter(new Commiter("kaboom"))

        then:
        thrown(InvalidDataAccessApiUsageException)
    }
}
