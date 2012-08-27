package codereview

import grails.converters.JSON

class ProjectController {

    def create(String url, String name) {
        def errorMessages = [
                "url.invalid": "is incorrect",
                unique: "already exists, choose something different",
                "size.toosmall": "should have more than 2 characters",
                "size.toobig": "should be shorter than 80 characters"
        ]

        Project project = new Project(name, url)
        if (project.validate()){
            project.save()
            render([message: "Project added successfully.",
                    errors: ""] as JSON)
        }
        else {
            render([message: "Adding failed...",
                    errors: project.errors.fieldError.collect {it.field + " " +errorMessages[it.code]}.join("\n")] as JSON)
        }
    }

    def remove(String name) {
        def project = Project.findByName(name)
        if(project.delete()){
            render([message: "Project deleted"] as JSON)
        }
        else{
            render([message: "Project couldn't be deleted"] as JSON)
        }
    }

    def names() {
        render( Project.all.collect{[name: it.name]} as JSON)
    }
}
