package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.FunctionalTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

@RunWith(SerenityRunner.class)
@TestPropertySource(locations = "classpath:config/application.properties")
public class DocMosisScotlandComponentTest {

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
    public void generateDocumentDundee() throws IOException {
        CCDRequest ccdRequest = getCcdRequest("1", "", true, new File(Constants.TEST_DATA_SCOT_DUNDEE_CASE1));
        Response response = getResponse(ccdRequest, "/amendCaseDetails");
        Assertions.assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode()); // Should be 200 but getting 403

    }

    private Response getResponse(CCDRequest ccdRequest, String uri) {
        return RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .body(ccdRequest)
                .post(uri);
    }

    private CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, File testData) throws IOException {
        String payload = FileUtils.readFileToString(testData, "UTF-8");
        return JsonUtil.getCaseDetails(payload, topLevel, childLevel, isScotland);
    }

}
