package codereview

class CommentedChangesetsFilterService implements FilterServiceInterface {

    @Override
    def getLastFilteredChangesets() {
        return Changeset.findAll("from Changeset c where userComments.size > 0 or \
                                    exists (from ProjectFileInChangeset p where p.changeset = c and \
                                    exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and pos.thread.comments.size > 0)) \
                                    order by c.date desc", [max: Constants.FIRST_LOAD_CHANGESET_NUMBER]);
    }

    @Override
    def getNextFilteredChangesets(Long changesetId) {
        def lastChangeset = Changeset.get(changesetId);
        return Changeset.findAll("from Changeset c where (userComments.size > 0 or \
                                    exists (from ProjectFileInChangeset p where p.changeset = c and \
                                    exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and pos.thread.comments.size > 0))) and c.date < :lastChangesetDate \
                                    order by c.date desc", [max: Constants.NEXT_LOAD_CHANGESET_NUMBER, lastChangesetDate: lastChangeset.date]);
    }

}
