package airboat


class InfrastructureService {

    File getProjectRoot(String scmUrl) {
        new File(workingDirectory, directoryNameForProject(scmUrl))
    }

    private File getWorkingDirectory() {
        def overridenWorkingDirectoryPath = System.getProperty(Constants.AIRBOAT_WORKING_DIRECTORY_PROPERTY)
        if (overridenWorkingDirectoryPath != null) {
            new File(overridenWorkingDirectoryPath)
        } else {
            new File(System.getProperty('java.io.tmpdir'), 'airboat-work')
        }
    }

    private String directoryNameForProject(String scmUrl) {
        Project.findByUrl(scmUrl).name
    }
}
