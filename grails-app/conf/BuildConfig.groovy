grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits("global") {}
    log "error" // log level of Ivy resolver
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "http://download.eclipse.org/jgit/maven"
    }

    dependencies {
        compile "postgresql:postgresql:9.1-901.jdbc4"
        compile "com.google.guava:guava:12.0"
        compile "org.eclipse.jgit:org.eclipse.jgit:2.0.0.201206130900-r"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1" //TODO update to 1.8.0
        runtime ":resources:1.1.6"

        build ":tomcat:$grailsVersion"

        test ":spock:0.6"
    }
}

coverage {
    enabledByDefault = false
    xml = true
}
