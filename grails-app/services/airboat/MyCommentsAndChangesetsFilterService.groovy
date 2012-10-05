package airboat

import org.springframework.security.access.prepost.PreAuthorize
import grails.plugins.springsecurity.SpringSecurityService

class MyCommentsAndChangesetsFilterService implements FilterServiceInterface {

    SpringSecurityService springSecurityService

    static private def conditions =  "from Changeset changeset where \
                                        (exists (from UserComment comment where comment.changeset = changeset and comment.author = :user)) or \
                                        (changeset.commiter in (from Commiter where user = :user)) or \
                                        (exists (from ProjectFileInChangeset p where changeset.date = (select max(file.changeset.date) from ProjectFileInChangeset file where file.projectFile = p.projectFile) and \
                                        exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and :user in (select author from LineComment where thread = pos.thread))))"

    static private def order = "order by changeset.date desc"

    @Override
    @PreAuthorize('isAuthenticated()')
    def getLastFilteredChangesets(String additionalInfo) {
        return Changeset.findAll(conditions + order, [max: Constants.FIRST_CHANGESET_LOAD_SIZE, user: springSecurityService.getCurrentUser()]);
    }

    @Override
    @PreAuthorize('isAuthenticated()')
    def getNextFilteredChangesets(Long changesetId, String additionalInfo) {
        def lastChangeset = Changeset.get(changesetId);
        return Changeset.findAll(conditions + " and changeset.date < :lastChangesetDate " + order, [max: Constants.FIRST_CHANGESET_LOAD_SIZE, user: springSecurityService.getCurrentUser(), lastChangesetDate: lastChangeset.date]);
    }
}