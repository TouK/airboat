package airboat

class FileFilterService implements FilterServiceInterface {

    static private def conditions = "select f.changeset from $ProjectFileInChangeset.name as f where f.projectFile.name like :fileName "

    static private def order = " order by f.changeset.date desc "

    @Override
    def getLastFilteredChangesets(String additionalInfo) {
        return ProjectFileInChangeset.executeQuery(conditions + order, [max: Constants.FIRST_CHANGESET_LOAD_SIZE, fileName: '%'+additionalInfo+'%']);
    }

    @Override
    def getNextFilteredChangesets(Long changesetId, String additionalInfo) {
        def lastChangeset = Changeset.get(changesetId);
        return ProjectFileInChangeset.executeQuery(conditions + " and f.changeset.date < :lastChangesetDate " + order, [max: Constants.NEXT_CHANGESET_LOAD_SIZE, fileName: '%'+additionalInfo+'%', lastChangesetDate: lastChangeset.date]);
    }

}
