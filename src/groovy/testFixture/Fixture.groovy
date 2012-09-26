package testFixture

import codereview.Constants

class Fixture {

    static final String PROJECT_CODEREVIEW_NAME = 'airboat'
    public static final GString PROJECT_CODEREVIEW_ON_THIS_MACHINE_URL = "file://${new File(".").absolutePath}"

    static final String FIRST_COMMIT_HASH = 'bc01b629a766616b16d32109c90a16bede93bdb3'
    static final String FIRST_COMMIT_AUTHOR = 'Kacper Pietrasik <kpt@touk.pl>'
    static final String FIRST_COMMIT_COMMENT = 'initial commit'
    //TODO use better date creation api, NOT Calendar. JodaTime, maybe?
    static final Date FIRST_COMMIT_DATE = new Date(1341318912000)
    static final int LOWER_BOUND_FOR_NUMBER_OF_COMMITS = 90

    static final String PATH_TO_FILE_PRESENT_IN_FIRST_COMMIT = 'grails-app/conf/Config.groovy'
    public static final String FIRST_LINE_OF_FILE_PRESENT_IN_FIRST_COMMIT = "// locations to search for config files that get merged into the main config"

    public static final String APPLICATION_PROPERTIES_FILE_NAME = 'application.properties'
    public static final String SECOND_COMMIT_INCLUDING_APPLICATION_PROPERTIES = 'b31743259b8e58cf3743cda44b15a98536a2be1f'
    public static final int SECOND_COMMIT_INCLUDINF_APPLICATION_PROPERTIES_NUMBER = 3;
    public static final String APPLICATION_PROPERTIES_SECOND_LINE_IN_FIRST_COMMIT = "#Tue Jul 03 14:08:33 CEST 2012"
    public static final String APPLICATION_PROPERTIES_SECOND_LINE_IN_SECOND_COMMIT_INCLUDING_IT = "#Wed Jul 04 12:21:42 CEST 2012"

    public static final String FILE_IN_PONGLISH = 'grails-app/services/codereview/ScmAccessService.groovy'
    public static final String FIRST_COMMIT_WITH_FILE_IN_PONGLISH = '4f12caaa1ea83363ed4c43d397d1f873fd4242fb'
    public static final String LINE_WITH_PONGLISH_TEXT = ' * Deleguje operacje na projekcie w SCM do odpowiedniej implementacji w zależności od rodzaju repozutorium kodu.'
    public static final int LINE_WITH_PONGLISH_NUMBER = 6
}
