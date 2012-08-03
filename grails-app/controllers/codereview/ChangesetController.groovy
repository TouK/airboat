package codereview

import grails.converters.JSON
class ChangesetController {

    ScmAccessService scmAccessService

    def index() {
    }

    /**
     * TODO helper method for development phase
     */
    def initialCheckOut() {
        Project.all.each {
            log.info("Checking out project ${it.url}.")
            scmAccessService.checkoutProject(it.url)
        }
        redirect(views: '/index', params: params)
    }

    def getLastChangesets = {
        def query
        if(params.id == null){
            query = Changeset.list(max: 21, sort: 'date', order: 'desc')
        }
        else{
            query = getLastChagesetsFromProject(params.id)
        }
        render query as JSON
    }

    def getFileNamesForChangeset = {
        def changeset = Changeset.findByIdentifier(params.id)
        def files = ProjectFile.findAllByChangeset(changeset)
        render files as JSON
    }


    def getChangeset = {
        def changeset = Changeset.findByIdentifier(params.id)
        def changesetList = [changeset]
        render changesetList as JSON
    }

    def getNextFewChangesetsOlderThan = {
        def nextFewChangesets
        if(params.projectName != ""){
            nextFewChangesets = getNextFewChangesetsFromProject(nextFewChangesets,params.projectName,params.changesetId)
        }
        else{
            nextFewChangesets = getNextFewChangesetsFromAllProjects(nextFewChangesets,params.changesetId)
        }
        render nextFewChangesets as JSON
    }

    def addLineComments = {
        [params: params]
    }

    private List<Changeset> getLastChagesetsFromProject(String projectName) {
        def projectQuery
        def project = Project.findByName(projectName) //TODO examine number of queries and try to make it 1.
        projectQuery = Changeset.findAllByProject(project, [max: 21, sort: 'date', order: 'desc'])
        projectQuery
    }

    private List<Changeset> getNextFewChangesetsFromAllProjects(List<Changeset> nextFewChangesets, String changesetId) {
        nextFewChangesets = Changeset.where {
            date < property(date).of { identifier == changesetId }
        }.list(max: 10, sort: 'date', order: 'desc')
        nextFewChangesets
    }

    private List<Changeset> getNextFewChangesetsFromProject(List<Changeset> nextFewChangesets, String projectName, String changesetId) {
        nextFewChangesets = Changeset.findAll(
                "from Changeset as changeset \
                    where changeset.project.name = :projectName \
                    and date < (select c.date from Changeset as c where c.identifier = :changesetId) \
                ",
                [projectName: projectName, changesetId: changesetId],
                [sort: 'date', order: 'desc', max: 10]
        )
        nextFewChangesets
    }
}

