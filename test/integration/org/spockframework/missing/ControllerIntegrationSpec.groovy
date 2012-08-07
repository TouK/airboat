package org.spockframework.missing

import grails.plugin.spock.ControllerSpec
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.test.support.GrailsTestAutowirer
import org.codehaus.groovy.grails.test.support.GrailsTestTransactionInterceptor
import org.codehaus.groovy.grails.test.support.GrailsTestRequestEnvironmentInterceptor

public abstract class ControllerIntegrationSpec extends ControllerSpec {

    private transactionManager
    private transactionStatus
    private applicationContext = ApplicationHolder.application.mainContext
    private autowirer = new GrailsTestAutowirer(applicationContext)
    private transactionInterceptor = new GrailsTestTransactionInterceptor(applicationContext)
    private requestEnvironmentInterceptor = new GrailsTestRequestEnvironmentInterceptor(applicationContext)

    def setup() {
        autowirer.autowire(this)
        requestEnvironmentInterceptor.init()
        if (transactionInterceptor.isTransactional(this)) transactionInterceptor.init()
    }

    def cleanup() {
        transactionInterceptor.destroy()
        requestEnvironmentInterceptor.destroy()
    }
}