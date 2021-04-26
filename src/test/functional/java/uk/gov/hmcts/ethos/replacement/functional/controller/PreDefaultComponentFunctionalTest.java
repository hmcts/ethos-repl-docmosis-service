package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.FunctionalTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

@RunWith(SerenityRunner.class)
@TestPropertySource(locations = "classpath:config/application.properties")
public class PreDefaultComponentFunctionalTest {
    private String AUTH_TOKEN;
    private FuncHelper funcHelper;

    @Before
    public void setUp() throws IOException {
        funcHelper = new FuncHelper();
        useRelaxedHTTPSValidation();
    }

    @Test
    @Category(FunctionalTest.class)
    public void claimantIndividualEng() throws IOException {
        String payload = FileUtils.readFileToString(new File(Constants.TEST_DATA_PRE_DEFAULT1),"UTF-8");
        CCDRequest ccdRequest = funcHelper.getCcdRequest("1", "1", false, payload);
        AUTH_TOKEN = funcHelper.loadAuthToken();
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .body(ccdRequest)
            .post("/preDefaultValues")
            .then()
            .statusCode(200); // Should be 200
    }

    @Test
    @Category(FunctionalTest.class)
    public void preDefaultNoPayload() throws IOException {
        CCDRequest ccdRequest = new CCDRequest();
        AUTH_TOKEN = funcHelper.loadAuthToken();
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .body(ccdRequest)
            .post("/preDefaultValues")
            .then()
            .statusCode(500);
    }

    @Test
    @Category(FunctionalTest.class)
    public void invokePreDefaultAsGet() throws IOException {
        AUTH_TOKEN = funcHelper.loadAuthToken();
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .get("/preDefaultValues")
            .then()
            .statusCode(405);
    }

}
