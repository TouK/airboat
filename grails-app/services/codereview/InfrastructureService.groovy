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
        def customBaseDirectoryName = System.getProperty('codereview.workingDirectory')
        if (customBaseDirectoryName != null) {
            return customBaseDirectoryName;
        } else {
            return System.getProperty('java.io.tmpdir') + File.separator + 'codereview-work';
        }
    }

    File createWorkingDirectory(File baseDirectory, String scmUrl) {
        if (!baseDirectory.exists()) {
            log.warn('Directory ' + baseDirectory + ' does not exist. Creating one.')
            baseDirectory.mkdir()
        }

        File dir = new File(baseDirectory, getDirectoryNameForTheProject(scmUrl))
        if (dir.exists()) {
            return dir;
        }

        log.info('Directory ' + dir + ' does not exist. Creating one.')
        if (dir.mkdir()) {
            return dir;
        } else {
            throw new IllegalStateException('Failed to create directory: ' + dir);
        }
    }

    String getDirectoryNameForTheProject(String scmUrl) {
        Project.findByUrl(scmUrl).name
    }
}
