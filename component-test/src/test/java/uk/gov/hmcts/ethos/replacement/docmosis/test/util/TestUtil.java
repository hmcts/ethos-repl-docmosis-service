package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.CCDRequest;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TestUtil {

    private static String authToken;
    private String topLevel;
    private String childLevel;

    @Autowired
    public TestUtil() {
        if (authToken == null) authToken = ResponseUtil.getAuthToken();
    }

    public void executeGenerateDocumentTest(String topLevel, String childLevel) throws IOException, JAXBException, Docx4JException {
        executeGenerateDocumentTest(topLevel, childLevel, false);
    }

    public void executeGenerateDocumentTest(String topLevel, String childLevel, boolean isScotland) throws IOException, JAXBException, Docx4JException {

        this.topLevel = topLevel;
        this.childLevel = childLevel;

        String payLoad = FileUtils.readFileToString(new File(Constants.TEST_DATA_CASE1));
        CCDRequest ccdRequest = JsonUtil.getCaseDetails(payLoad, topLevel, childLevel);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Authorization", authToken);
        httpRequest.header("Content-Type", ContentType.JSON);
        httpRequest.body(ccdRequest);
        Response response = httpRequest.post(Constants.BASE_URL + Constants.URL_GEN_DOCUMENT);

        Assert.assertEquals(200, response.getStatusCode());

        Pattern pattern = Pattern.compile("http://127.0.0.1:3453/documents/[a-z0-9\\-]+/binary");
        String url = ResponseUtil.getUrlFromResponse(response);

        Assert.assertTrue(pattern.matcher(url).matches());

        String downloadedFilePath = FileUtil.downloadFileFromUrl(url, authToken);

        String templatePath;

        if (isScotland) templatePath = Constants.TEMPLATE_PATH_SCOT.replace("#VERSION#", topLevel);
        else templatePath = Constants.TEMPLATE_PATH_ENG.replace("#VERSION#", topLevel);

        compareDocuments(new File(templatePath), new File(downloadedFilePath), DocumentUtil.buildDocumentContent(ccdRequest.getCaseDetails(), "authToken"), isScotland);

    }

    private void compareDocuments(File expectedDocument, File actualDocument, String testData, boolean isScotland) throws JAXBException, Docx4JException, IOException {

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

        List<String> expectedTextElements = Docx4jUtil.getSelectedTextElementsFromDocument(expectedDocument, docVersion);
        List<String> actualTextElements = Docx4jUtil.getAllTextElementsFromDocument(actualDocument);

        int itemCount = 0;
        StringBuilder stringBuilder = new StringBuilder();

        Pattern patternStartTag = Pattern.compile("^[0-9a-zA-Z_]+>>$");

        for (String key : expectedTextElements) {
            if (key.startsWith("<<cs")  || key.startsWith("<<else") || key.startsWith("<<##") || patternStartTag.matcher(key).matches()) continue;

            Pattern pattern = Pattern.compile(".*<<([\\w]+).*");
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                key = matcher.group(1);

                if (key != null) {
                    String expectedData = JsonUtil.getValue(testData, key);
                    String actualData = actualTextElements.get(itemCount);
                    if (actualData.endsWith(",")) actualData = actualData.substring(0, actualData.lastIndexOf(','));

                    if (expectedData == null) Assert.fail("Expected value missing in test data for key: " + key);

                    if (!expectedData.equals(actualData)) stringBuilder.append("Expected: " + expectedData + "; Actual: " + actualData);
                }
//                else {
//                    Assert.assertEquals(key, actualTextElements.get(itemCount).trim());
//                }
            }

            itemCount++;
        }

        Assert.assertTrue("Below expected values doesn't match actual: \n" + stringBuilder.toString(), StringUtils.isEmpty(stringBuilder.toString()));
    }
}
