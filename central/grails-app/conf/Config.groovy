// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents  ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.assets.bundle=true // for development environment, already done for other 4/2
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
    mail { 
        host = "smtp.gmail.com"
        
        port = 587 
        username = "oumuoauthagent@gmail.com" 
        password = "" 
        props = ["mail.debug": "true", 
                "mail.smtp.protocol": "smtps", 
                "mail.smtp.auth": "true",
               // "mail.hostname":"smtp.1and1.com",
                "mail.smtp.starttls.enable": "true", 
            	// "mail.smtp.EnableSSL.enable":"true",
		// "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
		// "mail.smtp.socketFactory.fallback":"false",
                "mail.smtp.host": "smtp.gmail.com", 
                "mail.smtp.user": "oumuoauthagent@gmail.com", 
                "mail.smtp.password": ""] 
    }
}
// default from email
grails.mail.default.from="oumuoauthagent@gmail.com"

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j.main = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
    root {
        // info()
        info  'file', 'stdout'
        //debug  'file', 'stdout'
        additivity = true
    }
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%d %p %t [%c] - <%m>%n')
        // console name: "customAppender",
        rollingFile  name:'file', file:'igive.log', maxFileSize:'2000KB', 
        maxBackupIndex:'5',append:'true',layout:pattern(conversionPattern: '%d{[EEE, dd-MMM-yyyy @ HH:mm:ss.SSS]} [%t] %-5p %c %x - %m%n')
        // layout: pattern(conversionPattern: "%c{2} %m%n")
    }
    // for all domain controller etc.
    // debug customAppender:'central.controller'
    trace 'central'
    trace 'com.oumuo'
    // this used to be error (for below)
    warn   'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
    warn 'org.springframework.security'
    warn 'org.hibernate.SQL'
    warn 'org.hibernate.type.descriptor.sql.BasicBinder'
}

grails.plugin.springsecurity.securityConfigType = "InterceptUrlMap"

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.oumuo.UserLogin'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.oumuo.UserLoginAuthority'
grails.plugin.springsecurity.authority.className = 'com.oumuo.Authority'
grails.plugin.springsecurity.authority.groupAuthorityNameField = 'authorities'
grails.plugin.springsecurity.useRoleGroups = true

// security implications so change to true and fix logout
grails.plugin.springsecurity.logout.postOnly = false
// note this is plugin and not plugins
// grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/'
grails.plugin.springsecurity.successHandler.alwaysUseDefault = true
// exception grails.plugin.springsecurity.successHandler.defaultTargetUrl = 'controllerName/actionName'

// suggested for s2ui customization
// grails.plugin.springsecurity.ui.register.emailBody = '...'
// grails.plugin.springsecurity.ui.register.emailFrom = '...'
// grails.plugin.springsecurity.ui.register.emailSubject = '...'
// grails.plugin.springsecurity.ui.register.defaultRoleNames = ['ROLE_CUSTOMER']
// ach new user will be granted ROLE_USER after finalizing the registration 
// this used to be staticRules, changed to interceptURLmaps
// grails.plugin.springsecurity.controllerAnnotations.staticRules

grails.plugin.springsecurity.interceptUrlMap= [
	'/':                              ['permitAll'],
	'/index':                         ['permitAll'],
    '/dbconsole/**':                         ['permitAll'],
	'/index.gsp':                     ['permitAll'],
	'/assets/**':                     ['permitAll'],  // check for .txt files
	'/**/js/**':                      ['permitAll'],
	'/**/css/**':                     ['permitAll'],
	'/**/images/**':                  ['permitAll'],
	'/**/favicon.ico':                ['permitAll'],
        '/login/**':          ['permitAll'],
        '/logout/**':         ['permitAll'],
        '/register/**':                   ['permitAll'],\
        '/secure/**':         ['ROLE_ADMIN'],
        '/person/**':         ['ROLE_USER', 'ROLE_ADMIN'],
        '/account/**':         ['ROLE_USER', 'ROLE_ADMIN'],
        '/userlogin/**':         ['ROLE_ADMIN'],
        '/authority/**':         ['ROLE_ADMIN'],
        '/user/**':                       ['ROLE_ADMIN'],
        '/role/**':                       ['ROLE_ADMIN'],
        '/finance/**':        ['ROLE_FINANCE', 'isFullyAuthenticated()']
]


// grails.plugin.springsecurity.successHandler.defaultTargetUrl = 'controllerName/actionName'