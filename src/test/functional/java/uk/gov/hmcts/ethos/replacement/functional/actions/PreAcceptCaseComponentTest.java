package uk.gov.hmcts.ethos.replacement.functional.actions;

import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.junit.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.File;
import java.io.IOException;

public class PreAcceptCaseComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    /**
     * England & Wales */
    @Test
    public void create_eng_case_and_accept() throws IOException {
        testUtil.executePreAcceptCaseTest(Constants.TEST_DATA_ENG_CREATE_CASE1, Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_ACCEPT, "Accepted", false);

    }

    /**
     * England & Wales */
    @Test
    public void create_eng_case_and_reject() throws IOException {
        testUtil.executePreAcceptCaseTest(Constants.TEST_DATA_ENG_CREATE_CASE1, Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_REJECT, "Rejected", false);
    }

    /**
     * England & Wales */
    @Test
    public void try_and_reject_already_accepted_case_eng() throws IOException {
        String ethosCaseReference = testUtil.executePreAcceptCaseTest(Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_ACCEPTED,
                                    Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_ACCEPT, "Accepted", false);

        String caseDetails = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_ACCEPTED), "UTF-8");
        caseDetails = caseDetails.replace("#ETHOS-CASE-REFERENCE#", ethosCaseReference);
        caseDetails.replace("\"caseAccepted\":\"Yes\",", "\"caseAccepted\":\"No\",");

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", false, caseDetails);
        Response response = testUtil.getResponse(ccdRequest, Constants.PRE_ACCEPT_CASE);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        testUtil.verifyCaseStatus(caseDetails, response, "Accepted");
    }

    /**
     * England & Wales */
    @Test
    public void try_and_accept_already_rejected_case_eng() throws IOException {
        String ethosCaseReference = testUtil.executePreAcceptCaseTest(Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_REJECTED,
                Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_REJECT, "Rejected",false);

        String caseDetails = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_PRE_ACCEPT_CASE1_REJECTED), "UTF-8");
        caseDetails = caseDetails.replace("#ETHOS-CASE-REFERENCE#", ethosCaseReference);
        caseDetails.replace("\"caseAccepted\":\"No\",", "\"caseAccepted\":\"Yes\",");

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", false, caseDetails);
        Response response = testUtil.getResponse(ccdRequest, Constants.PRE_ACCEPT_CASE);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        testUtil.verifyCaseStatus(caseDetails, response, "Rejected");
    }

    /**
     * Scotland */
    @Test
    @Ignore
    public void create_scot_case_and_accept() throws IOException {
        testUtil.executePreAcceptCaseTest(Constants.TEST_DATA_SCOT_CREATE_CASE1, Constants.TEST_DATA_SCOT_PRE_ACCEPT_CASE1_ACCEPT, "Accepted",true);
    }

    /**
     * Scotland */
    @Test
    @Ignore
    public void try_and_accept_already_rejected_case_scot() throws IOException {
        String ethosCaseReference = testUtil.executePreAcceptCaseTest(Constants.TEST_DATA_SCOT_PRE_ACCEPT_CASE1_REJECTED,
                Constants.TEST_DATA_SCOT_PRE_ACCEPT_CASE1_REJECT, "Rejected", true);

        String caseDetails = FileUtils.readFileToString(new File(Constants.TEST_DATA_SCOT_PRE_ACCEPT_CASE1_REJECTED), "UTF-8");
        caseDetails = caseDetails.replace("#ETHOS-CASE-REFERENCE#", ethosCaseReference);
        caseDetails.replace("\"caseAccepted\":\"No\",", "\"caseAccepted\":\"Yes\",");

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", true, caseDetails);
        Response response = testUtil.getResponse(ccdRequest, Constants.PRE_ACCEPT_CASE);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        testUtil.verifyCaseStatus(caseDetails, response, "Rejected");
    }

    /**
     * Negative */
    @Test
    public void pre_accept_case_with_no_payload() throws IOException {
        testUtil.loadAuthToken();

        Response response = testUtil.getResponse(new CCDRequest(), Constants.PRE_ACCEPT_CASE, 500);
    }

    /**
     * Negative */
    @Test
    @Ignore
    public void pre_accept_case_with_invalid_token() throws IOException {

        testUtil.setAuthToken("authToken");

        String caseDetails = FileUtils.readFileToString(new File(Constants.TEST_DATA_SCOT_PRE_ACCEPT_CASE1_ACCEPT), "UTF-8");
        caseDetails = caseDetails.replace("#ETHOS-CASE-REFERENCE#", "985654698546");

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", false, caseDetails);
        Response response = testUtil.getResponse(ccdRequest, Constants.PRE_ACCEPT_CASE);

        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusCode());

        testUtil.setAuthToken(null);
    }


    @After
    public void tearDown() {
    }
}
