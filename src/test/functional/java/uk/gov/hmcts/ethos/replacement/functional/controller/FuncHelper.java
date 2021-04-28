package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static uk.gov.hmcts.ethos.replacement.functional.util.ResponseUtil.getProperty;

public class FuncHelper {

    @Value("${test-url}")
    private String testUrl;

    private String AUTH_TOKEN;

    public FuncHelper() throws IOException {
        baseURI = getProperty("demo.docmosis.api.url");
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, String testData)
            throws IOException {
        return JsonUtil.getCaseDetails(testData, topLevel, childLevel, isScotland);
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, File testDataFile)
            throws IOException {
        String payload = FileUtils.readFileToString(testDataFile, "UTF-8");
        return getCcdRequest(topLevel, childLevel, isScotland, payload);
    }

    public String createIndividualCase(List<String> caseList, boolean isScotland, String testData) throws IOException {
        int count = 1;
        String ethosCaseRef = "";
        //loadAuthToken();
        for(String caseDataFilePath: caseList) {
            ethosCaseRef = RandomStringUtils.randomNumeric(10);
            String caseDetails = FileUtils.readFileToString(new File(caseDataFilePath), "UTF-8");
            caseDetails = caseDetails.replace("#ETHOS-CASE-REFERENCE#", ethosCaseRef);
            CCDRequest ccdRequest = getCcdRequest("1", "", isScotland, caseDetails);
            Response response = RestAssured.given() // Status code currently 500
                    .header(HttpHeaders.AUTHORIZATION, "eyJhbGJbpjciOiJIUzI1NiJ9")
                    .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                    .body(ccdRequest)
                    .post("/createCase");
//            Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
            count++;
        }
        return testData.replace("#ETHOS-CASE-REFERENCE" + count + "#", ethosCaseRef);
    }


    public Response getBulkResponse(BulkRequest bulkRequest, String uri) throws IOException {
        loadAuthToken();
        return RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, "AUTH_TOKEN")
                .header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
                .body(bulkRequest)
                .post(uri);
    }

    public Response getCcdResponse(CCDRequest ccdRequest, String uri) throws IOException {
        loadAuthToken();
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

    public String loadAuthToken() throws IOException {
        if(AUTH_TOKEN == null) {
            AUTH_TOKEN = getAuthToken();
        }
        if(!AUTH_TOKEN.startsWith("Bearer")) {
            AUTH_TOKEN = "Bearer " + AUTH_TOKEN;
        }
        return AUTH_TOKEN;
    }

    public String getAuthToken() throws IOException {
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Accept", "*/*");
        httpRequest.header("Content-Type", "application/x-www-form-urlencoded");
        httpRequest.formParam("username", getProperty("demo.ccd.username"));
        httpRequest.formParam("password",  getProperty("demo.ccd.password"));
        Response response = httpRequest.post(getProperty("demo.idam.auth.url"));
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
        return response.body().jsonPath().getString("access_token");
    }
}
