package codereview

interface FilterServiceInterface {
    def getLastFilteredChangesets()
    def getNextFilteredChangesets(Long changesetId)
}
