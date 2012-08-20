package codereview


class InfrastructureService {

    public File getProjectWorkingDirectory(String scmUrl) {
        File baseDir = baseDirectory
        createWorkingDirectory(baseDir, scmUrl)
    }

    File getBaseDirectory() {
        new File(baseDirectoryName);
    }

    String getBaseDirectoryName() {
        def customBaseDirectoryName = System.getProperty('codereview.workingDirectory')
        if (customBaseDirectoryName != null) {
            return customBaseDirectoryName;
        } else {
            return System.getProperty('java.io.tmpdir') + File.separator + 'codereview-work';
        }
    }

    File createWorkingDirectory(File baseDirectory, String scmUrl) {
        checkBaseDirectory(baseDirectory)
        return checkProjectDirectory(baseDirectory, scmUrl)
    }

    private File checkProjectDirectory(File baseDirectory, String scmUrl) {
        File projectDirectory = new File(baseDirectory, getDirectoryNameForTheProject(scmUrl))
        if (projectDirectory.exists()) {
            return projectDirectory;
        }
        return createProjectDirectory(projectDirectory)
    }

    private File createProjectDirectory(File projectDirectory) {
        log.info('Directory ' + projectDirectory + ' does not exist. Creating one.')
        if (projectDirectory.mkdir()) {
            return projectDirectory;
        } else {
            throw new IllegalStateException('Failed to create directory: ' + projectDirectory);
        }
    }

    private void checkBaseDirectory(File baseDirectory) {
        if (!baseDirectory.exists()) {
            createBaseDirectory(baseDirectory)
        }
    }

    private void createBaseDirectory(File baseDirectory) {
        log.warn('Directory ' + baseDirectory + ' does not exist. Creating one.')
        baseDirectory.mkdir()
    }

    String getDirectoryNameForTheProject(String scmUrl) {
        Project.findByUrl(scmUrl).name
    }
    String getFullPathForProjectWorkingDirectory(String projectName) {
        getBaseDirectoryName()  + File.separator + projectName
    }
}
