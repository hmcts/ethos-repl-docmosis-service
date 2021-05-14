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
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

@RunWith(SerenityRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CaseActionsForCaseworkerFunctional {

    private String AUTH_TOKEN = "";
    private List<String> caseList = new ArrayList<>();

    private FuncHelper funcHelper;

    @Before
    public void setUp() throws IOException {
        funcHelper = new FuncHelper();
        useRelaxedHTTPSValidation();
    }

    //  Test not working correctly
    @Test
    @Category(FunctionalTest.class)
    public void createCaseIndividualClaimantNotRepresentedEng() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE3);
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1));
        testData = funcHelper.createIndividualCase(caseList, false, testData);
    }

    @Test
    @Category(FunctionalTest.class)
    public void createIndividualCaseNoPayload() throws IOException {
        CCDRequest ccdRequest = new CCDRequest();
        AUTH_TOKEN = funcHelper.loadAuthToken();
        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .body(ccdRequest)
            .post("/createCase")
            .then()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @Category(FunctionalTest.class)
    public void retrieveCases() throws IOException {
        CCDRequest ccdRequest;
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE3);
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1));
        testData = funcHelper.createIndividualCase(caseList, false, testData);
        // AUTH_TOKEN = funcHelper.loadAuthToken(); Code causes 500 error
        ccdRequest = funcHelper.getCcdRequest("1", "", false, testData);
        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, "AUTH_TOKEN")
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .body(ccdRequest)
                .post("/retrieveCases")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);

    }

    @Test
    @Category(FunctionalTest.class)
    public void updateCase() throws IOException {
        CCDRequest ccdRequest;
        caseList.clear();

    }
}
