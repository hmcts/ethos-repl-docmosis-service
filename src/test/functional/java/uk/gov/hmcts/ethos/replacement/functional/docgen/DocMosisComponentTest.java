package uk.gov.hmcts.ethos.replacement.functional.docgen;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.ResponseUtil;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.IOException;
import java.net.URL;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTag("ComponentTest")
public class DocMosisComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void verify_payload_eng_claimant_individual_not_represented() throws Exception {
        testUtil.verifyDocMosisPayload("10", "1", false, Constants.TEST_DATA_CASE1);
    }

    @Test
    public void verify_payload_eng_claimant_company_not_represented() throws Exception {
        testUtil.verifyDocMosisPayload("10", "1", false, Constants.TEST_DATA_CASE2);
    }

    @Test
    public void verify_payload_eng_claimant_individual_represented() throws Exception {
        testUtil.verifyDocMosisPayload("10", "1", false, Constants.TEST_DATA_CASE3);
    }

    @Test
    public void verify_payload_eng_respondant_represented() throws Exception {
        testUtil.verifyDocMosisPayload("10", "1", false, Constants.TEST_DATA_CASE4);
    }

    @Test
    public void verify_payload_sco_claimant_individual_not_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "", true, Constants.TEST_DATA_SCOT_CASE1);
    }

    @Test
    public void verify_payload_sco_claimant_company_not_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "", true, Constants.TEST_DATA_SCOT_CASE2);
    }

    @Test
    public void verify_payload_sco_claimant_individual_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "", true, Constants.TEST_DATA_SCOT_CASE3);
    }

    @Test
    public void verify_payload_sco_respondant_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "", true, Constants.TEST_DATA_SCOT_CASE4);
    }

    @Test
    @WithTag("SmokeTest")
    public void verify_document_eng_claimant_individual_not_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "1", "Mr A Banderas", false, Constants.TEST_DATA_CASE1);
    }

    @Test
    @WithTag("FunctionalTest")
    public void verify_document_eng_claimant_company_not_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "1", "Acme Logistics Ltd", false, Constants.TEST_DATA_CASE2);
    }

    @Test
    @WithTag("FunctionalTest")
    public void verify_document_eng_claimant_individual_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "1", "Mr A Banderas", false, Constants.TEST_DATA_CASE3);
    }

    @Test
    @WithTag("FunctionalTest")
    public void verify_document_eng_respondant_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "1", "Mr A Banderas", false, Constants.TEST_DATA_CASE4);
    }

    @Test
    @WithTag("SmokeTest")
    public void verify_document_sco_claimant_individual_not_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", "Mr A Banderas", true, Constants.TEST_DATA_SCOT_CASE1);
    }

    @Test
    @WithTag("FunctionalTest")
    public void verify_document_sco_claimant_company_not_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", "Acme Logictics Ltd", true, Constants.TEST_DATA_SCOT_CASE2);
    }

    @Test
    @WithTag("FunctionalTest")
    public void verify_document_sco_claimant_individual_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", "Mr A Banderas", true, Constants.TEST_DATA_SCOT_CASE3);
    }

    @Test
    @WithTag("FunctionalTest")
    public void verify_document_sco_respondant_represented() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", "Mr A Banderas", true, Constants.TEST_DATA_SCOT_CASE4);
    }

    @Ignore
    @Test
    @WithTag("FunctionalTest")
    public void invoke_pre_default_endpoint_with_invalid_auth_token() throws IOException {
        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "1", false, Constants.TEST_DATA_CASE1);

        try {
            testUtil.setAuthToken("Bearer authToken");
            Response response = testUtil.getResponse(ccdRequest, Constants.DOCGEN_URI, 401);
        } finally {
            testUtil.setAuthToken(null);
        }
    }

    @Ignore
    @Test
    @WithTag("FunctionalTest")
    public void invoke_pre_default_endpoint_without_payload() throws IOException {
        testUtil.loadAuthToken();

        CCDRequest ccdRequest = new CCDRequest();

        Response response = testUtil.getResponse(ccdRequest, Constants.DOCGEN_URI, 400);
    }

    @Test
    @WithTag("FunctionalTest")
    public void invoke_pre_default_as_get() throws IOException {
        String docmosisUrl = ResponseUtil.getProperty(testUtil.getEnvironment().toLowerCase() + ".docmosis.api.url");
        SerenityRest.given().get(new URL(docmosisUrl + Constants.DOCGEN_URI)).then().statusCode(405);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
