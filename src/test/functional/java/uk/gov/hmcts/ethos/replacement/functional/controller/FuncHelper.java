package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.baseURI;

public class FuncHelper {

    @Value("${test-url}")
    private String testUrl;

    private String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";

    public FuncHelper() {
        baseURI = "http://ethos-repl-docmosis-backend-demo.service.core-compute-demo.internal";
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, String testData)
            throws IOException {
        return JsonUtil.getCaseDetails(testData, topLevel, childLevel, isScotland);
    }

    public String createIndividualCase(List<String> caseList, boolean isScotland, String testData) throws IOException {
        int count = 1;
        String ethosCaseRef = "";
        for(String caseDataFilePath: caseList) {
            ethosCaseRef = RandomStringUtils.randomNumeric(10);
            String caseDetails = FileUtils.readFileToString(new File(caseDataFilePath), "UTF-8");
            caseDetails = caseDetails.replace("#ETHOS-CASE-REPLACEMENT#", ethosCaseRef);
            CCDRequest ccdRequest = getCcdRequest("1", "", isScotland, caseDetails);

            System.out.println("CREATE CASE ---> " + ccdRequest.getCaseDetails().getCaseId());

            RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                    .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                    .body(ccdRequest)
                    .post("/createCase")
                    .then()
                    .statusCode(HttpStatus.SC_FORBIDDEN); // Should be 200
            count++;
        }
        return testData.replace("#ETHOS-CASE-REFERENCE" + count + "#", ethosCaseRef);
    }


    public Response getBulkResponse(BulkRequest bulkRequest, String uri) {
        return RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .body(bulkRequest)
                .post(uri);
    }

    public Response getCcdResponse(CCDRequest ccdRequest, String uri) {
        return RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .body(ccdRequest)
                .post(uri);
    }

    public void postDefaultValues(String parameter, String value, String testData) throws IOException {
        CCDRequest ccdRequest = getCcdRequest("1", "", false, testData);
        Response response = getCcdResponse(ccdRequest, "/postDefaultValues");
        String json = response.body().prettyPrint();

    }
}
