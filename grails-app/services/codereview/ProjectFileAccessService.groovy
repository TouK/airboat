package codereview

class ProjectFileAccessService {

    String fetchFileContentFromPath(String fileName, String projectDir) {

        def file = new File(projectDir + fileName)
        def content
        if(file.exists())
            content = file.getText()
        else
            content  = "File doesn't exist"

        return content
    }

}
