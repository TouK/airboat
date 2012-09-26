package codereview

import org.springframework.security.access.prepost.PreAuthorize
import grails.plugins.springsecurity.SpringSecurityService

class MyCommentsAndChangesetsFilterService implements FilterServiceInterface {

    SpringSecurityService springSecurityService

    @Override
    @PreAuthorize('isAuthenticated()')
    def getLastFilteredChangesets() {
        return Changeset.findAll("from Changeset changeset where \
                                        (exists (from UserComment comment where comment.changeset = changeset and comment.author = :user)) or \
                                        (changeset.commiter in (from Commiter where user = :user)) or  \
                                        (exists (from ProjectFileInChangeset p where changeset.date = (select max(file.changeset.date) from ProjectFileInChangeset file where file.projectFile = p.projectFile) and \
                                            exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and :user in (select author from LineComment where thread = pos.thread))))\
                                        order by changeset.date desc", [max: Constants.FIRST_LOAD_CHANGESET_NUMBER, user: springSecurityService.getCurrentUser()]);
    }

    @Override
    @PreAuthorize('isAuthenticated()')
    def getNextFilteredChangesets(Long changesetId) {
        def lastChangeset = Changeset.get(changesetId);
        return Changeset.findAll("from Changeset changeset where \
                                        ((exists (from UserComment comment where comment.changeset = changeset and comment.author = :user)) or \
                                        (changeset.commiter in (from Commiter where user = :user)) or  \
                                        (exists (from ProjectFileInChangeset p where changeset.date = (select max(file.changeset.date) from ProjectFileInChangeset file where file.projectFile = p.projectFile) and \
                                            exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and :user in (select author from LineComment where thread = pos.thread))))) and changeset.date < :lastChangesetDate\
                                        order by changeset.date desc", [max: Constants.FIRST_LOAD_CHANGESET_NUMBER, user: springSecurityService.getCurrentUser(), lastChangesetDate: lastChangeset.date]);
    }
}