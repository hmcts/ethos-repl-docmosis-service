package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.FunctionalTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;

import java.io.File;
import java.io.IOException;

import static net.serenitybdd.rest.RestDefaults.useRelaxedHTTPSValidation;

@RunWith(SerenityRunner.class)
@TestPropertySource(locations = "classpath:config/application.properties")
public class PostDefaultFunctionalTest {
    private FuncHelper funcHelper;
    private String AUTH_TOKEN;

    @Before
    public void setUp() throws IOException {
        funcHelper = new FuncHelper();
        useRelaxedHTTPSValidation();
    }

    @Test
    @Category(FunctionalTest.class)
    public void claimantIndividualEng() throws IOException {
        CCDRequest ccdRequest = funcHelper.getCcdRequest("1", "", false, new File(Constants.TEST_DATA_POST_DEFAULT1));
        Response response = funcHelper.getCcdResponse(ccdRequest, "/postDefaultValues");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }

    @Test
    @Category(FunctionalTest.class)
    public void postDefaultNoPayload() throws IOException {
        CCDRequest ccdRequest = new CCDRequest();
        AUTH_TOKEN = funcHelper.loadAuthToken();
        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .body(ccdRequest)
                .post("/postDefaultValues")
                .then()
                .statusCode(500);
    }

    @Test
    @Category(FunctionalTest.class)
    public void invokePostDefaultAsGet() throws IOException {
        AUTH_TOKEN = funcHelper.loadAuthToken();
        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .get("/postDefaultValues")
                .then()
                .statusCode(405);
    }

}
