package airboat

class FileFilterService implements FilterServiceInterface {

    static private def conditions = "select distinct f.changeset, f.changeset.date from $ProjectFileInChangeset.name as f where f.projectFile.name like :fileName "

    static private def order = " order by f.changeset.date desc "

    @Override
    def getLastFilteredChangesets(String additionalInfo) {
        def changesetsandDates = ProjectFileInChangeset.executeQuery(conditions + order, [max: Constants.FIRST_CHANGESET_LOAD_SIZE, fileName: '%'+additionalInfo+'%'])
        return changesetsandDates.collect{it[0]}
    }

    @Override
    def getNextFilteredChangesets(Long changesetId, String additionalInfo) {
        def lastChangeset = Changeset.get(changesetId);
        def changesetsAndDates = ProjectFileInChangeset.executeQuery(conditions + " and f.changeset.date < :lastChangesetDate " + order,
                [max: Constants.NEXT_CHANGESET_LOAD_SIZE, fileName: '%'+additionalInfo+'%', lastChangesetDate: lastChangeset.date])
        return changesetsAndDates.collect{it[0]}
    }

}
