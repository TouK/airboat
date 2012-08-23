package codereview


class InfrastructureService {

    File getProjectRoot(String scmUrl) {
        new File(workingDirectory, directoryNameForProject(scmUrl))
    }

    private File getWorkingDirectory() {
        def overridenWorkingDirectoryPath = System.getProperty('codereview.workingDirectory')
        if (overridenWorkingDirectoryPath != null) {
            new File(overridenWorkingDirectoryPath)
        } else {
            new File(System.getProperty('java.io.tmpdir'), 'codereview-work')
        }
    }

    private String directoryNameForProject(String scmUrl) {
        Project.findByUrl(scmUrl).name
    }
}
