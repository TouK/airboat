package airboat

import grails.validation.Validateable

@Validateable
class FilterCommand {

    String filterType
    String additionalInfo

    static constraints = {
        filterType blank: false
    }
}