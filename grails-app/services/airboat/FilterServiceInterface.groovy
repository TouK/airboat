package airboat

interface FilterServiceInterface {
    def getLastFilteredChangesets()
    def getNextFilteredChangesets(Long changesetId)
}
