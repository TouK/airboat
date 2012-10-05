package airboat

interface FilterServiceInterface {
    def getLastFilteredChangesets(String additionalInfo)
    def getNextFilteredChangesets(Long changesetId, String additionalInfo)
}
