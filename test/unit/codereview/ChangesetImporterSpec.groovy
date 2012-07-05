package codereview

import spock.lang.Specification
import grails.test.mixin.Mock
import testFixture.Fixture

@Mock(Changeset)
class ChangesetImporterSpec extends Specification {

    def "should fetch and save changesets in db"() {
        given:
            def (gitScmUrl, changesetId, changesetAuthor)  = [Fixture.PROJECT_REPOSITORY_URL, "id123", "agj"]
            GitRepository gitRepository = Mock()
            gitRepository.fetchChangelog(gitScmUrl) >> [new Changeset(changesetId, changesetAuthor, new Date())]
            ChangesetImporter importer = new ChangesetImporter(gitRepository)

        when:
            importer.importFrom(gitScmUrl)

        then:
            Changeset.count() == 1
            Changeset.findAllByIdentifierAndAuthor(changesetId, changesetAuthor).size() == 1
    }


}
