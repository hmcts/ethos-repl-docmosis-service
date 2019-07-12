package uk.gov.hmcts.ethos.replacement.functional;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HealthCheckTest {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckTest.class);

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

    @Test
    @Category(SmokeTest.class)
    public void healthcheck_returns_200() {
        assertThat("smokeTest", is("smokeTest"));
        get("/health").then().statusCode(200);
    }
}
