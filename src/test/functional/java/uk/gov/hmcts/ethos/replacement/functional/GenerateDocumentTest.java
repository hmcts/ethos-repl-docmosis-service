package uk.gov.hmcts.ethos.replacement.functional;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.get;

public class GenerateDocumentTest {

    private static final Logger log = LoggerFactory.getLogger(GenerateDocumentTest.class);

    @Before
    public void before() {
//        String appUrl = System.getenv("TEST_URL");
//        if (appUrl == null) {
//            appUrl = "http://localhost:8081";
//        }
//
//        RestAssured.baseURI = appUrl;
//        RestAssured.useRelaxedHTTPSValidation();
//        log.info("Base Url set to: " + RestAssured.baseURI);
    }

    //@Test
    public void generate_document_returns_405() {
        //TODO
        get("/generateDocument").then().statusCode(405);
    }
}
