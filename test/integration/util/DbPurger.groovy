package util

import codereview.Project
import org.codehaus.groovy.grails.commons.ApplicationHolder
import codereview.ProjectFile
import codereview.User
import codereview.Commiter

class DbPurger {

    static void verifyDbIsClean() {
        Project.withNewSession {
            domainClasses().each {
                if (it.count() != 0) {
                    throw new IllegalStateException("Db is not clean - ${it}.count() is ${it.count()}")
                }
            }
        }
    }

    private static List<Class<?>> domainClasses() {
        ApplicationHolder.application.domainClasses*.clazz
    }

    static void purgeDb() {
        Project.withNewSession {
            ProjectFile.all.each { it.delete(flush: true) }
            Project.all.each { Project project ->
                project.delete(flush: true)
            }
            User.all.each { User user ->
                user.committers*.user = null
                user.committers = [] as Set
                user.delete(flush: true)
            }
            Commiter.all.each {
                it.delete(flush: true)
            }
        }
    }
}
