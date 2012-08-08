package codereview

class ProjectFileAccessService {

    String fetchFileContentFromPath(String projectDir, String fileName) {

        def file = new File(projectDir, fileName)
        def content
        if (file.exists())
            content = file.getText()
        else
            content = "File doesn't exist" //FIXME not like that, return null (!?) or throw an exception

        return content
    }

    def getFileContent(ProjectFile projectFile, projectRootDirectory) {
        def path = projectRootDirectory.getAbsolutePath()
        return fetchFileContentFromPath(path, projectFile.name)
    }
}
