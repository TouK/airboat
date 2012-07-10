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
        def customWorkingDirectoryName = System.getProperty("codereview.workingDirectory")
        if (customWorkingDirectoryName != null) {
            return customWorkingDirectoryName;
        } else {
            return System.getProperty("java.io.tmpdir");
        }
    }

    File createWorkingDirectory(File baseDirectory, String scmUrl) {

        if(!baseDirectory.exists()) {
            log.warn("Directory " + baseDirectory + " does not exist. Creating one.")
            baseDirectory.mkdir()
        }

        File dir = new File(baseDirectory, File.separator + getDirectoryNameForTheProject(scmUrl))
        if (dir.exists()) {
            return dir;
        }

        if (dir.mkdir()) {
            log.info("Directory " + dir + " does not exist. Creating one.")
            return dir;
        }

        throw new IllegalStateException("Failed to create directory: " + dir);
    }

    String getDirectoryNameForTheProject(String scmUrl) {
        "projekt"
    }


}
