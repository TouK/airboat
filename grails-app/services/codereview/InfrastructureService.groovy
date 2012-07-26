package codereview

class InfrastructureService {

    public File getProjectWorkingDirectory(String scmUrl) {
        File baseDir = getBaseDirectory()
        createWorkingDirectory(baseDir, scmUrl)
    }

    File getBaseDirectory() {
        return new File(getBaseDirectoryName());
    }

    String getBaseDirectoryName() {
        def customBaseDirectoryName = System.getProperty("codereview.workingDirectory")        //TODO: change "codereview.workingDirectory"" on variable
        if (customBaseDirectoryName != null) {       //TODO: should be method instead of flag
            return customBaseDirectoryName;
        } else {
            return System.getProperty("java.io.tmpdir");                     //TODO: change "java..." on variable
        }
    }

    File createWorkingDirectory(File baseDirectory, String scmUrl) {
        if(!baseDirectory.exists()) {
            log.warn("Directory " + baseDirectory + " does not exist. Creating one.")
            baseDirectory.mkdir()
        }

        File dir = new File(baseDirectory, getDirectoryNameForTheProject(scmUrl))
        if (dir.exists()) {
            return dir;
        }

        log.info("Directory " + dir + " does not exist. Creating one.")
        if (dir.mkdir()) {
            return dir;
        } else {
            throw new IllegalStateException("Failed to create directory: " + dir);
        }
    }

    /**
     * TODO implement later on
     * @param scmUrl
     * @return
     */
    String getDirectoryNameForTheProject(String scmUrl) {
        "projekt"                                        //TODO: change on variable
    }


}
