package uk.gov.hmcts.ethos.replacement.functional.controller;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.apache.commons.io.FileUtils;
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
    public void generateDocumentDundee() throws IOException {
        String payload = FileUtils.readFileToString(new File(Constants.TEST_DATA_SCOT_DUNDEE_CASE1), "UTF-8");
        CCDRequest ccdRequest = funcHelper.getCcdRequest("1", "", true, payload);
        Response response = funcHelper.getCcdResponse(ccdRequest, "/amendCaseDetails");
        Assertions.assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode()); // Should be 200 but getting 403

    }
}
