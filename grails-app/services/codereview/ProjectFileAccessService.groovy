package codereview

class ProjectFileAccessService {

    String fetchFileContentFromPath(String projectName, String fileName) {
        def infrastructureService = new InfrastructureService()
        def PATH = infrastructureService.getFullPathForProjectWorkingDirectory(projectName) + File.separator + fileName
        def file = new File(PATH)
        def content
        if (file.exists())
            content = file.getText()
        else
            content = "File doesn't exist" //FIXME not like that, return null (!?) or throw an exception

        return content
    }

    def getFileContent(ProjectFile projectFile, projectName) {
        return fetchFileContentFromPath(projectName, projectFile.name)
    }
}
