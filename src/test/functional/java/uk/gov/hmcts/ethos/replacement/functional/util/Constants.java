package uk.gov.hmcts.ethos.replacement.functional.util;

public class Constants {

    //General constants
    public static final String DOWNLOAD_FOLDER = "src/test/functional/resources/documents";
    public static final String CONFIG_PROPERTIES = "src/test/functional/resources/config.properties";
    public static final String URL_PATTERN = "http.*://.*/documents/[a-z0-9\\-]+/binary";

    //URI
    public static final String TOKEN_URI = "/testing-support/lease?id=1&role=ccd-import";
    public static final String DOCGEN_URI = "/generateDocument";
    public static final String PRE_DEFAULT_URI = "/preDefaultValues";
    public static final String POST_DEFAULT_URI = "/postDefaultValues";
    public static final String CREATE_BULK_URI = "/createBulk";
    public static final String SEARCH_BULK_URI = "/searchBulk";
    public static final String UPDATE_BULK_URI = "/updateBulk";
    public static final String UPDATE_BULK_CASE_URI = "/updateBulkCase";



    //Test Case Data files
    public static final String TEST_DATA_CASE1 = "src/test/functional/resources/testData/caseDetailsTest1.json";
    public static final String TEST_DATA_CASE2 = "src/test/functional/resources/testData/caseDetailsTest2.json";
    public static final String TEST_DATA_CASE3 = "src/test/functional/resources/testData/caseDetailsTest3.json";
    public static final String TEST_DATA_CASE4 = "src/test/functional/resources/testData/caseDetailsTest4.json";
    public static final String TEST_DATA_SCOT_CASE1 = "src/test/functional/resources/testData/caseDetailsScotTest1.json";
    public static final String TEST_DATA_SCOT_CASE2 = "src/test/functional/resources/testData/caseDetailsScotTest2.json";
    public static final String TEST_DATA_SCOT_CASE3 = "src/test/functional/resources/testData/caseDetailsScotTest3.json";
    public static final String TEST_DATA_SCOT_CASE4 = "src/test/functional/resources/testData/caseDetailsScotTest4.json";

    public static final String TEST_DATA_PRE_DEFAULT1 = "src/test/functional/resources/testData/defaults/preDefaultTest1.json";
    public static final String TEST_DATA_PRE_DEFAULT2 = "src/test/functional/resources/testData/defaults/preDefaultTest2.json";
    public static final String TEST_DATA_SCOT_PRE_DEFAULT1 = "src/test/functional/resources/testData/defaults/preDefaultScotTest1.json";
    public static final String TEST_DATA_SCOT_PRE_DEFAULT2 = "src/test/functional/resources/testData/defaults/preDefaultScotTest2.json";

    public static final String TEST_DATA_POST_DEFAULT1 = "src/test/functional/resources/testData/defaults/postDefaultTest1.json";
    public static final String TEST_DATA_POST_DEFAULT2 = "src/test/functional/resources/testData/defaults/postDefaultTest2.json";
    public static final String TEST_DATA_POST_DEFAULT3 = "src/test/functional/resources/testData/defaults/postDefaultTest1.json";
    public static final String TEST_DATA_POST_DEFAULT4 = "src/test/functional/resources/testData/defaults/postDefaultTest2.json";
    public static final String TEST_DATA_SCOT_POST_DEFAULT1 = "src/test/functional/resources/testData/defaults/postDefaultScotTest1.json";
    public static final String TEST_DATA_SCOT_POST_DEFAULT2 = "src/test/functional/resources/testData/defaults/postDefaultScotTest2.json";

    public static final String TEST_DATA_BULK1 = "src/test/functional/resources/testData/exampleBulkV1.json";


}
