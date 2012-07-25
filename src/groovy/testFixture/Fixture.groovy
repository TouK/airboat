package testFixture

class Fixture {

    public static final String FIRST_COMMIT_HASH = "bc01b629a766616b16d32109c90a16bede93bdb3"
    public static final String FIRST_COMMIT_AUTHOR = "Kacper Pietrasik <kpt@touk.pl>"
    public static final String FIRST_COMMIT_COMMENT = "initial commit"
    //TODO use better date creation api, NOT Calendar. JodaTime, maybe?
    public static final Date FIRST_COMMIT_DATE = new Date(1341318912000)
    public static final int LOWER_BOUND_FOR_NUMBER_OF_COMMITS = 90
    public static final String PROJECT_REPOSITORY_URL = Constants.PROJECT_REPOSITORY_URL

}
