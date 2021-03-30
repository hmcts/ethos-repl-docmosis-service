package uk.gov.hmcts.ethos.replacement.functional;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.get;

public class GenerateDocumentTest {

    private static final Logger log = LoggerFactory.getLogger(GenerateDocumentTest.class);

    @Before
    public void before() {
    }

    public void generate_document_returns_405() {
        get("/generateDocument").then().statusCode(405);
    }
}
