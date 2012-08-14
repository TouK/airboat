package codereview

class ProjectFileAccessService {

    String fetchFileContentFromPath(String projectDir, String fileName) {

        def file = new File(projectDir, fileName)
        def content
        if (file.exists()) {
            content = file.text
        }
        else {
            content = "File doesn't exist" //FIXME not like that, return null (!?) or throw an exception
        }
        content
    }

    def getFileContent(ProjectFile projectFile, projectRootDirectory) {
        def path = projectRootDirectory.getAbsolutePath()
        fetchFileContentFromPath(path, projectFile.name)
    }
}
