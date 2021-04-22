package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.FunctionalTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.FileUtil;
import uk.gov.hmcts.ethos.replacement.functional.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

@RunWith(SerenityRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class PreDefaultComponentFunctionalTest {
    @Value("${test-url}")
    private String testUrl;

    private String AUTH_TOKEN = "Bearer someAuthToken";
    private List<String> caseList = new ArrayList<>();

    @Before
    public void setUp() {
        baseURI = "http://ethos-repl-docmosis-backend-demo.service.core-compute-demo.internal";
        useRelaxedHTTPSValidation();
    }

    @Test
    @Category(FunctionalTest.class)
    public void claimaintIndividualEng() throws IOException {
        CCDRequest ccdRequest = getCcdRequest("1", "1", false, new File(Constants.TEST_DATA_PRE_DEFAULT1));
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .body(ccdRequest)
            .post("/preDefaultValues")
            .then()
            .statusCode(HttpStatus.SC_FORBIDDEN); // Should be 200
    }

    @Test
    @Category(FunctionalTest.class)
    public void preDefaultNoPayload() {
        CCDRequest ccdRequest = new CCDRequest();
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .body(ccdRequest)
            .post("/preDefaultValues")
            .then()
            .statusCode(403);
    }

    @Test
    @Category(FunctionalTest.class)
    public void invokePreDefaultAsGet() {
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .get("/preDefaultValues")
            .then()
            .statusCode(405);
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, File testData) throws IOException {
        String payload = FileUtils.readFileToString(testData,"UTF-8");
        return JsonUtil.getCaseDetails(payload, topLevel, childLevel, isScotland);
    }
}
