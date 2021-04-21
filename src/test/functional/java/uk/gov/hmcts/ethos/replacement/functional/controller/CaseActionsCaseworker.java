package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
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
@TestPropertySource(locations = "classpath:application.properties")
public class CaseActionsCaseworker {

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
    public void createCaseIndividualClaimantNotRepresentedEng() throws IOException {
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);

        for(String caseDataFilePath: caseList) {
            String ethosCaseRef = RandomStringUtils.randomNumeric(10);
            String caseDetails = FileUtils.readFileToString(new File(caseDataFilePath), "UTF-8");
            caseDetails = caseDetails.replace("#ETHOS-CASE-REPLACEMENT#", ethosCaseRef);
            CCDRequest ccdRequest = getCcdRequest("1", "", false, caseDetails);

            System.out.println("CREATE CASE ---> " + ccdRequest.getCaseDetails().getCaseId());

            RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                    .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                    .body(ccdRequest)
                    .post("/createCase")
                    .then()
                    .statusCode(HttpStatus.SC_FORBIDDEN);
        }
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, String testData)
            throws IOException {
        return JsonUtil.getCaseDetails(testData, topLevel, childLevel, isScotland);
    }

    @Test
    @Category(FunctionalTest.class)
    public void createBulkNoCasesEng() throws IOException {
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK6), "UTF-8");
        BulkRequest bulkRequest = JsonUtil.getBulkDetails(true, testData);
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .body(bulkRequest)
            .post("/createBulk")
            .then()
            .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    public String getAuthToken() {
        RestAssured.useRelaxedHTTPSValidation();
        return null;
    }
}
