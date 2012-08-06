package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture
import org.apache.maven.scm.ChangeSet

@Mock([Project, Changeset, ProjectFile, Commiter, User])
class ScmAccessServiceSpec extends Specification {

    def "should fetch and save changesets in db"() {
        given:
            new Project("codereview", Fixture.PROJECT_REPOSITORY_URL).save()
            def (gitScmUrl, changesetId, commitComment, changesetAuthor)  = [Fixture.PROJECT_REPOSITORY_URL, "id", "comment", "agj@touk.pl"]
            ScmAccessService scmAccessService = new ScmAccessService()

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
}
