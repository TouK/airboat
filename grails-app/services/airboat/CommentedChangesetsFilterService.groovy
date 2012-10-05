package airboat

class CommentedChangesetsFilterService implements FilterServiceInterface {

    static private def conditions = "from Changeset c where userComments.size > 0 or \
                                    exists (from ProjectFileInChangeset p where c.date = (select max(file.changeset.date) from ProjectFileInChangeset file where file.projectFile = p.projectFile) and \
                                    exists (from ThreadPositionInFile pos where pos.projectFileInChangeset = p and pos.thread.comments.size > 0))"

    static private def order = "order by c.date desc"

    @Override
    def getLastFilteredChangesets(String additionalInfo) {
        return Changeset.findAll(conditions + order, [max: Constants.FIRST_CHANGESET_LOAD_SIZE]);
    }

    @Override
    def getNextFilteredChangesets(Long changesetId, String additionalInfo) {
        def lastChangeset = Changeset.get(changesetId);
        return Changeset.findAll(conditions + " and c.date < :lastChangesetDate " + order, [max: Constants.NEXT_CHANGESET_LOAD_SIZE, lastChangesetDate: lastChangeset.date]);
    }

}
