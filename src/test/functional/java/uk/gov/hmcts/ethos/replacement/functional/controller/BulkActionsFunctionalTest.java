package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.functional.FunctionalTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

public class BulkActionsFunctionalTest {
    @Value("${test-url}")
    private String testUrl;

    private String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private List<String> caseList = new ArrayList<>();

    private FuncHelper funcHelper;

    @Before
    public void setUp() {
        funcHelper = new FuncHelper();
        baseURI = "http://ethos-repl-docmosis-backend-demo.service.core-compute-demo.internal";
        useRelaxedHTTPSValidation();
    }

    @Test
    @Category(FunctionalTest.class)
    public void createBulkNoCasesEng() throws IOException {
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK6), "UTF-8");
        BulkRequest bulkRequest = JsonUtil.getBulkDetails(true, testData);
        Response response = funcHelper.getBulkResponse(bulkRequest, "/createBulk");
        Assertions.assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Category(FunctionalTest.class)
    public void createBulkNoPayload() {
        BulkRequest bulkRequest = new BulkRequest();
        Response response = funcHelper.getBulkResponse(bulkRequest, "/createBulk");
        Assertions.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Category(FunctionalTest.class)
    public void searchBulkTest() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1));
        testData = funcHelper.createIndividualCase(caseList, false, testData);
        BulkRequest bulkRequest = JsonUtil.getBulkDetails(false, testData);
        Response response = funcHelper.getBulkResponse(bulkRequest, "/createBulk");
        Assertions.assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode());
        Response searchResponse = funcHelper.getBulkResponse(bulkRequest, "/searchBulk");
        Assertions.assertEquals(HttpStatus.SC_FORBIDDEN, searchResponse.getStatusCode());
    }

}
