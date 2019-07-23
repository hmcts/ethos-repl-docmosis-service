package uk.gov.hmcts.ethos.replacement.functional.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.json.JSONException;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtil {

    private static String authToken;
    private String topLevel;
    private String childLevel;

    private String environment;

    private static String downloadedFilePath;

    public TestUtil() {
        environment = System.getProperty("VAULTNAME").replace("ethos-", "");
    }

    //End-point /generateDocument
    public void executeGenerateDocumentTest(String topLevel, String childLevel, String expectedValue) throws IOException, JAXBException, Docx4JException {
        executeGenerateDocumentTest(topLevel, childLevel, expectedValue, false);
    }

    public void executeGenerateDocumentTest(String topLevel, String childLevel, String expectedValue, boolean isScotland) throws IOException, JAXBException, Docx4JException {
        if (isScotland) executeGenerateDocumentTest(topLevel, childLevel, expectedValue, true, Constants.TEST_DATA_SCOT_CASE1);
        else executeGenerateDocumentTest(topLevel, childLevel, expectedValue, false, Constants.TEST_DATA_CASE1);
    }

    public void executeGenerateDocumentTest(String topLevel, String childLevel, String expectedValue, boolean isScotland, String testData) throws IOException, JAXBException, Docx4JException {

        this.topLevel = topLevel;
        this.childLevel = childLevel;

        loadAuthToken();

        CCDRequest ccdRequest;

        if (isScotland) ccdRequest = getCcdRequest(topLevel, childLevel, true, new File(testData));
        else ccdRequest = getCcdRequest(topLevel, childLevel, false, new File(testData));

        Response response = getResponse(ccdRequest);

        verifyDocument(topLevel, expectedValue, isScotland, ccdRequest, response);

    }

    public void verifyDocMosisPayload(String topLevel, String childLevel, boolean isScotland, String testDataFile) throws IOException, JSONException {
        this.topLevel = topLevel;
        this.childLevel = childLevel;

        if (authToken == null) authToken = ResponseUtil.getAuthToken(environment);
        if (!authToken.startsWith("Bearer")) authToken = "Bearer " + authToken;

        CCDRequest ccdRequest = getCcdRequest(topLevel, childLevel, isScotland, new File(testDataFile));

        Response response = getResponse(ccdRequest);
        String url = ResponseUtil.getUrlFromResponse(response);
        downloadedFilePath = FileUtil.downloadFileFromUrl(url, authToken);

        String actualPayload = LogUtil.getDocMosisPayload();
        actualPayload = actualPayload.substring(0, actualPayload.lastIndexOf(',')) + "}}";

        String expectedPayload = DocumentUtil.buildDocumentContent(ccdRequest.getCaseDetails(), "");

        JSONAssert.assertEquals(expectedPayload, actualPayload, JSONCompareMode.LENIENT);
    }

    //End-point /preDefaultValues
    public void executePreDefaultValuesTest(String paramName, String paramValue, boolean isScotland, String testData) throws IOException {
        CCDRequest ccdRequest;

        loadAuthToken();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, new File(testData));
        else ccdRequest = getCcdRequest("1", "", false, new File(testData));

        Response response = getResponse(ccdRequest, Constants.PRE_DEFAULT_URI);

        String json = response.body().prettyPrint();

        verifyElementValue(json, paramName, paramValue);
    }

    //End-point /postDefaultValues
    public void executePostDefaultValuesTest(String paramName, String paramValue, boolean isScotland, String testData) throws IOException {
        CCDRequest ccdRequest;

        loadAuthToken();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, testData);
        else ccdRequest = getCcdRequest("1", "", false, testData);

        Response response = getResponse(ccdRequest, Constants.POST_DEFAULT_URI);

        String json = response.body().prettyPrint();

        verifyElementValue(json, paramName, paramValue);
    }

    //End-point /createBulk
    public void executeCreateBulkTest(boolean isScotland, String testData) throws IOException {
        CCDRequest ccdRequest;

        loadAuthToken();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, new File(testData));
        else ccdRequest = getCcdRequest("1", "", false, new File(testData));

        Response response = getResponse(ccdRequest, Constants.CREATE_BULK_URI);

        verifyCreateBulkResponse(testData, response);

    }

    //End-point /searchBulk
    public void executeSearchBulkTest(boolean isScotland, String testData) throws IOException {
        CCDRequest ccdRequest;

        loadAuthToken();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, new File(testData));
        else ccdRequest = getCcdRequest("1", "", false, new File(testData));

        Response response = getResponse(ccdRequest, Constants.SEARCH_BULK_URI);

        verifySearchBulkResponse(testData, response);
    }

    //End-point /updateBulk
    public void executeUpdateBulkTest(boolean isScotland, String testData) throws IOException {
        CCDRequest ccdRequest;

        loadAuthToken();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, new File(testData));
        else ccdRequest = getCcdRequest("1", "", false, new File(testData));

        Response response = getResponse(ccdRequest, Constants.SEARCH_BULK_URI);

        String json = response.body().prettyPrint();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, json);
        else ccdRequest = getCcdRequest("1", "", false, json);

        response = getResponse(ccdRequest, Constants.UPDATE_BULK_URI);

        verifyUpdateBulkResponse(testData, response);
    }

    //End-point //updateBulkCase
    public void executeUpdateBulkCaseTest(boolean isScotland, String testData) throws IOException {
        CCDRequest ccdRequest;

        loadAuthToken();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, new File(testData));
        else ccdRequest = getCcdRequest("1", "", false, new File(testData));

        Response response = getResponse(ccdRequest, Constants.CREATE_BULK_URI);

        String json = response.body().prettyPrint();

        if (isScotland) ccdRequest = getCcdRequest("1", "1", true, json);
        else ccdRequest = getCcdRequest("1", "", false, json);

        response = getResponse(ccdRequest, Constants.UPDATE_BULK_CASE_URI);

        verifyUpdateBulkCaseResponse(testData, response);
    }

    //General methods
    public String getEnvironment() {
        return this.environment;
    }

    public void deleteTempFile() throws IOException {
        if (downloadedFilePath != null) {
            FileUtils.forceDelete(new File(downloadedFilePath));
            downloadedFilePath = null;
        }
    }

    public Response getResponse(CCDRequest ccdRequest) throws IOException {
        return getResponse(ccdRequest, Constants.DOCGEN_URI);
    }

    public Response getResponse(CCDRequest ccdRequest, String URI) throws IOException {
        return getResponse(ccdRequest, URI, 200);
    }

    public Response getResponse(CCDRequest ccdRequest, String URI, int expectedStatusCode) throws IOException {
        String docmosisUrl = ResponseUtil.getProperty(environment.toLowerCase() + ".docmosis.api.url");

        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
        RequestSpecification httpRequest = SerenityRest.given().relaxedHTTPSValidation().config(RestAssured.config);
        httpRequest.header("Authorization", authToken);
        httpRequest.header("Content-Type", ContentType.JSON);
        httpRequest.body(ccdRequest);
        Response response = httpRequest.post(docmosisUrl + URI);

        Assert.assertEquals(expectedStatusCode, response.getStatusCode());
        return response;
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, File testDataFile) throws IOException {
        String payLoad = FileUtils.readFileToString(testDataFile, "UTF-8");

        return JsonUtil.getCaseDetails(payLoad, topLevel, childLevel, isScotland);
    }

    public CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland, String testData) throws IOException {
        return JsonUtil.getCaseDetails(testData, topLevel, childLevel, isScotland);
    }

    public void setAuthToken(String authToken) {
        TestUtil.authToken = authToken;
    }

    public String loadAuthToken() throws IOException {
        if (authToken == null) authToken = ResponseUtil.getAuthToken(environment);
        if (!authToken.startsWith("Bearer")) authToken = "Bearer " + authToken;

        return authToken;
    }

    //Private methods
    private void verifyElementValue(String json, String paramName, String paramValue) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode childNode = rootNode.findValue(paramName);

        if (childNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> elements = childNode.fields();
            for (;elements.hasNext(); ) {
                Map.Entry<String, JsonNode> element = elements.next();
                Assert.assertTrue(paramValue.contains(element.getValue().textValue()));
            }
        } else {
            Assert.assertEquals(paramValue, childNode.textValue());
        }
    }

    private void verifyDocument(String topLevel, String expectedValue, boolean isScotland, CCDRequest ccdRequest, Response response) throws IOException, JAXBException, Docx4JException {
        Pattern pattern = Pattern.compile(Constants.URL_PATTERN);
        String url = ResponseUtil.getUrlFromResponse(response);

        Assert.assertNotNull("Null URL returned", url);
        Assert.assertTrue(pattern.matcher(url).matches());

        downloadedFilePath = FileUtil.downloadFileFromUrl(url, authToken);

        existsInDocument(expectedValue, new File(downloadedFilePath), isScotland);
    }

    private CCDRequest getCcdRequest(String topLevel, String childLevel, boolean isScotland) throws IOException {
        return getCcdRequest(topLevel, childLevel, isScotland, Constants.TEST_DATA_CASE1);
    }

    private void existsInDocument(String expectedValue, File actualDocument, boolean isScotland) throws JAXBException, Docx4JException {

        String docVersion;

        if (isScotland) {
            if (StringUtils.isEmpty(this.topLevel)) docVersion = "";
            else {
                if (StringUtils.isEmpty(this.childLevel)) docVersion = "Scot_" + this.topLevel;
                else docVersion = "Scot_" + this.topLevel + "_" + this.childLevel;
            }
        } else {
            if (StringUtils.isEmpty(this.childLevel)) docVersion = "";
            else docVersion = this.topLevel + "_" + this.childLevel;
        }

        List<String> actualTextElements = Docx4jUtil.getAllTextElementsFromDocument(actualDocument);

        boolean hasMatched=false;
        for (String key : actualTextElements) {
            Pattern pattern = Pattern.compile(".*" + expectedValue + ".*");
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                hasMatched=true;
                break;
            }
        }

        Assert.assertTrue("Expected value \""+ expectedValue + "\" doesn't exist in Document with version: "+  docVersion +" \n", hasMatched);
    }

    private void verifyCreateBulkResponse(String testData, Response response) {

    }

    private void verifySearchBulkResponse(String testData, Response response) {

    }

    private void verifyUpdateBulkResponse(String testData, Response response) {

    }

    private void verifyUpdateBulkCaseResponse(String testData, Response response) {

    }
}
