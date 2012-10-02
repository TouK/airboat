package util

import airboat.Project
import org.codehaus.groovy.grails.commons.ApplicationHolder
import airboat.ProjectFile
import airboat.User
import airboat.Commiter
import airboat.ThreadPositionInFile
import airboat.CommentThread

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
            [ThreadPositionInFile, ProjectFile, CommentThread, Project].each { deleteAll(it) }
            User.all.each { User user ->
                user.committers*.user = null
                user.committers = [] as Set
                user.delete(flush: true)
            }
            deleteAll(Commiter)
        }
    }

    private static void deleteAll(Class domainClass) {
        domainClass.all*.delete(flush: true)
    }
}
