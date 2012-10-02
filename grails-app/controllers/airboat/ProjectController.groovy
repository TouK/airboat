package airboat

import grails.converters.JSON

class ProjectController {

    def create(String url, String name) {
        def errorMessages = [
                "url.invalid": "is incorrect",
                unique: "already exists, use different one",
                "size.toosmall": "should have at least 2 characters",
                "size.toobig": "should be shorter than 80 characters",
                nullable: "can't be empty",
                blank: "can't be empty"
        ]

        Project project = new Project(name, url)
        if (project.validate()){
            project.save()
            render([message: "Project added successfully.",
                    errors: ""] as JSON)
        }
        else {
            render([message: "Adding failed...",
                    errors: project.errors.fieldErrors.collect {[message: it.field+ " " +errorMessages[it.code]]  }] as JSON)
        }
    }

    def remove(String name) {
        def project = Project.findByName(name)
        project.delete(flush: true)
        if(!Project.findByName(name)){
            render([message: " deleted"] as JSON)
        }
        else{
            render([message: " couldn't be deleted"] as JSON)
        }
    }

    def names() {
        render( Project.all.sort{Project it -> it.name}.collect{[name: it.name]} as JSON)
    }
}
