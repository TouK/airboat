package testFixture

import codereview.Constants

class Fixture {

    static final String FIRST_COMMIT_HASH = 'bc01b629a766616b16d32109c90a16bede93bdb3'
    static final String FIRST_COMMIT_AUTHOR = 'Kacper Pietrasik <kpt@touk.pl>'
    static final String FIRST_COMMIT_COMMENT = 'initial commit'
    //TODO use better date creation api, NOT Calendar. JodaTime, maybe?
    static final Date FIRST_COMMIT_DATE = new Date(1341318912000)
    static final int LOWER_BOUND_FOR_NUMBER_OF_COMMITS = 90
    static final String PROJECT_CODEREVIEW_NAME = 'codereview'
    static final String PROJECT_CODEREVIEW_REPOSITORY_URL = Constants.PROJECT_CODEREVIEW_REPOSITORY_URL
    static final String PATH_TO_FILE_PRESENT_IN_FIRST_COMMIT = 'grails-app/conf/Config.groovy'
}
