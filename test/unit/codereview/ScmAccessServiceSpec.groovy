package codereview

import grails.test.mixin.Mock
import spock.lang.Specification
import testFixture.Fixture
import org.apache.maven.scm.ChangeSet
import grails.buildtestdata.mixin.Build



@Build(Project)
@Mock([Project, Changeset, ProjectFile, Commiter, User])
class ScmAccessServiceSpec extends Specification {

    def "should fetch and save changesets in db"() {
        given:
            Project project = Project.build()
            def (changesetId, commitComment) = ["hash23", "commitin"]
            ChangeSet changeSet = new ChangeSet(new Date(), commitComment, "Artur Gajowy <agj@touk.pl>", [])
            changeSet.setRevision(changesetId)

            ScmAccessService scmAccessService = new ScmAccessService()
            scmAccessService.gitRepositoryService = Mock(GitRepositoryService)
            1 * scmAccessService.gitRepositoryService.getAllChangeSets(project.url) >> [ changeSet ]

        when:
            scmAccessService.importAllChangesets(project.url)

        then:
            Changeset.count() == 1
            Changeset.findAllByIdentifierAndCommitComment(changesetId, commitComment).size() == 1
    }

    //TODO this testing is incomplete, because service has got many methods and they're aren't tested anywhere - More tests!

    //FIXME test for sort order of changesets
}
