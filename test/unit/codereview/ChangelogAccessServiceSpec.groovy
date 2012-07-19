package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture

@Mock(Changeset)
class ChangelogAccessServiceSpec extends Specification {

    def "should fetch and save changesets in db"() {
        given:
            def (gitScmUrl, changesetId, commitComment, changesetAuthor)  = [Fixture.PROJECT_REPOSITORY_URL, "id", "comment", "agj@touk.pl"]
            GitRepositoryService gitRepositoryMock = Mock()
            gitRepositoryMock.fetchNewChangelog(Fixture.PROJECT_REPOSITORY_URL) >> [new Changeset(changesetId, changesetAuthor, commitComment, new Date())]
            ChangelogAccessService cas = new ChangelogAccessService()
            cas.gitRepositoryService = gitRepositoryMock

        when:
            cas.fetchChangelogAndSave(gitScmUrl)

        then:
            //1 * gitRepositoryMock.updateProject(gitScmUrl)
            Changeset.count() == 1
            Changeset.findAllByIdentifierAndAuthor(changesetId, changesetAuthor).size() == 1
    }


}
