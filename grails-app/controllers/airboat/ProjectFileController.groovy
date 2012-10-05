package airboat

import grails.converters.JSON

import static com.google.common.base.Preconditions.checkArgument

//FIXME add tests
class ProjectFileController {

    def scmAccessService
    def snippetWithCommentsService
    def diffAccessService
    def threadPositionConverterService


    def index() { }

    def showLastProjectFiles() {
        def files = ProjectFile.list(max: 20)
        render files as JSON
    }

    def getFileListings(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        def fileContent = projectFileInChangeset.changeType == ChangeType.DELETE ? null : scmAccessService.getFileContent(changeset, projectFile)
        def diff = diffAccessService.getDiffWithPreviousRevisionFor(changeset, projectFile)

        render([diff: diff,
                fileContent: fileContent,
                fileType: projectFile.fileType,
                isText: projectFile.textFormat] as JSON)
    }

    def getThreadPositionAggregatesForFile(String changesetIdentifier, Long projectFileId) {
        def changeset = Changeset.findByIdentifier(changesetIdentifier)
        def projectFile = ProjectFile.findById(projectFileId)
        def projectFileInChangeset = ProjectFileInChangeset.findByChangesetAndProjectFile(changeset, projectFile)
        checkArgument(projectFileInChangeset != null, "${projectFile} is not associated with ${changeset}")

        def threadPositionsProperties = threadPositionConverterService.getThreadPositionsProperties(projectFileInChangeset)
        def threadPositionsWithSnippets = snippetWithCommentsService.addSnippetsToThreadPositions(threadPositionsProperties, projectFileInChangeset)
        render(threadPositionsWithSnippets as JSON)
    }

}

