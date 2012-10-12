import org.apache.log4j.Level

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [ "classpath:mail-config.groovy" ]
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << 'file:' + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        less: 'text/less',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = 'none' // none, html, base64
grails.views.gsp.encoding = 'UTF-8'

grails.converters.encoding = 'UTF-8'
grails.converters.json.pretty.print = true

// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

//default value of save's failOnError
grails.gorm.failOnError = true

// enable query caching by default
grails.hibernate.cache.queries = true

//Spring Security plugin configuration:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'airboat.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'airboat.UserRole'
grails.plugins.springsecurity.authority.className = 'airboat.Role'
grails.plugins.springsecurity.apf.usernameParameter = 'email'

grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/login/successful'
grails.plugins.springsecurity.successHandler.alwaysUseDefault = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = 'http://www.changeme.com'
    }
}

// log4j configuration
log4j = {

    appenders {

        def catalinaBase = System.properties.getProperty('catalina.base')
        if (!catalinaBase) catalinaBase = '.'
        def logDirectory = "${catalinaBase}/logs"

        console name: 'stdout', layout: pattern(conversionPattern: '%d{HH:mm:ss} %p %c %m%n')
        rollingFile name: 'stacktrace', layout: pattern(conversionPattern: '%d{MM-dd-yyyy HH:mm:ss} %p %c >>%m%n'),
                maxFileSize: 20480, file: "${logDirectory}/AirboatStackTrace.log"
        rollingFile name: 'rollingFileQuartz', layout: pattern(conversionPattern: '%d{MM-dd-yyyy HH:mm:ss} %p %c >>%m%n'),
                maxFileSize: 20480, file: "${logDirectory}/JobExecutionException.log"
    }

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            //'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            //'org.hibernate',
            //'net.sf.ehcache.hibernate',
            'airboat',
            'grails.app'

//    trace 'org.hibernate.type'
//        //'org.hibernate.engine.Cascade'
//
//    debug 'airboat',
//            'grails.app',
//            'org.hibernate.engine',
//            'org.hibernate.SQL',
//            'org.hibernate.event'


    environments {
        production {
            root {
                info 'stdout'
                error 'stacktrace'
            }
            all additivity: false
            all rollingFileQuartz: 'org.quartz'
        }
        development {
            root {
                debug 'stdout'
                error 'stacktrace'
            }
            all additivity: false
            all rollingFileQuartz: 'org.quartz'
        }
        test {
            root {
                debug 'stdout'
                error 'stacktrace'
            }
            all additivity: false
            all rollingFileQuartz: 'org.quartz'
        }
    }
}

coverage {
    enabledByDefault = false
    xml = true
}