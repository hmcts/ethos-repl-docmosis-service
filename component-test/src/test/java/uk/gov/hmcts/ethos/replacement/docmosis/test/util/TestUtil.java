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
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TestUtil {

    private AuthTokenGenerator authTokenGenerator;

    private static String authToken;

    @Autowired
    public TestUtil(AuthTokenGenerator authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
        authToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJvM3JkazRyYW1ub3Q3bGNidmljMzltMmowMCIsInN1YiI6IjIyIiwiaWF0IjoxNTU4MDAwMjMzLCJleHAiOjE1NTgwMjkwMzMsImRhdGEiOiJjYXNld29ya2VyLGNhc2V3b3JrZXItdGVzdCxjYXNld29ya2VyLXB1YmxpY2xhdy1sb2NhbEF1dGhvcml0eSxjYXNld29ya2VyLXB1YmxpY2xhdyxjYXNld29ya2VyLXB1YmxpY2xhdy1jb3VydGFkbWluLGNhc2V3b3JrZXIsY2FzZXdvcmtlci1sb2ExLGNhc2V3b3JrZXItdGVzdC1sb2ExLGNhc2V3b3JrZXItcHVibGljbGF3LWxvY2FsQXV0aG9yaXR5LWxvYTEsY2FzZXdvcmtlci1wdWJsaWNsYXctbG9hMSxjYXNld29ya2VyLXB1YmxpY2xhdy1jb3VydGFkbWluLWxvYTEsY2FzZXdvcmtlci1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiMjIiLCJmb3JlbmFtZSI6IkVyaWMiLCJzdXJuYW1lIjoiQ29vcGVyIiwiZGVmYXVsdC1zZXJ2aWNlIjoiQ0NEIiwibG9hIjoxLCJkZWZhdWx0LXVybCI6Imh0dHBzOi8vbG9jYWxob3N0OjkwMDAvcG9jL2NjZCIsImdyb3VwIjoiY2FzZXdvcmtlciJ9.g2OQeDfUdkO-QGHbV71r53M4uF-hj2r9-ew2rFlQiTg";
        if (authToken == null) authToken = this.authTokenGenerator.generate();
    }

    public void executeGenerateDocumentTest(String topLevel, String childLevel) throws IOException, JAXBException, Docx4JException {
        executeGenerateDocumentTest(topLevel, childLevel, false);
    }

    public void executeGenerateDocumentTest(String topLevel, String childLevel, boolean isScotland) throws IOException, JAXBException, Docx4JException {

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

        String templateVersion = topLevel + "." + childLevel;
        if (isScotland) templatePath = Constants.TEMPLATE_PATH_SCOT.replace("#VERSION#", templateVersion);
        else templatePath = Constants.TEMPLATE_PATH_ENG.replace("#VERSION#", templateVersion);

        compareDocuments(new File(templatePath), new File(downloadedFilePath), DocumentUtil.buildDocumentContent(ccdRequest.getCaseDetails(), "authToken"));

    }

    private void compareDocuments(File expectedDocument, File actualDocument, String testData) throws JAXBException, Docx4JException, IOException {

        List<String> expectedTextElements = Docx4jUtil.getAllTextElementsFromDocument(expectedDocument);
        List<String> actualTextElements = Docx4jUtil.getAllTextElementsFromDocument(actualDocument);

        int itemCount = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : expectedTextElements) {
            if (key.startsWith("<<cs")  || key.startsWith("<<else") || key.startsWith("<<##")) continue;

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
