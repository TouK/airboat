package codereview

import grails.converters.JSON

class ProjectController {

    def create(String url, String name) {
        def project = new Project(name, url)
        if (project.validate()){
            project.save()
            render([message: "Project added successfully."] as JSON)
        }
        else {
            render([message: "Adding failed. " +
                    project.errors.fieldError.collect {it.field + ": " + it.rejectedValue + " was invalid,  code: " + it.code}.join("\n")] as JSON)
        }
    }

    def remove(String name) {
        def project = Project.findByName(name)
        project.delete()
        render([message: "Project deleted"] as JSON)
    }

    def names() {
        render( Project.all.collect{[name: it.name]} as JSON)
    }
}
