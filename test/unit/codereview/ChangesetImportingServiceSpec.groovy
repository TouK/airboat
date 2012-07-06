package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture

@Mock(Changeset)
class ChangesetImportingServiceSpec extends Specification {

    def "should fetch and save changesets in db"() {
        given:
            def (gitScmUrl, changesetId, changesetAuthor)  = [Fixture.PROJECT_REPOSITORY_URL, "id123", "agj"]
            GitRepositoryService gitRepositoryMock = Mock()
            gitRepositoryMock.fetchChangelog(gitScmUrl) >> [new Changeset(changesetId, changesetAuthor, new Date())]
            ChangesetImportingService importer = new ChangesetImportingService()
            importer.gitRepositoryService = gitRepositoryMock

        when:
            importer.importFrom(gitScmUrl)

        then:
            Changeset.count() == 1
            Changeset.findAllByIdentifierAndAuthor(changesetId, changesetAuthor).size() == 1
    }


}
