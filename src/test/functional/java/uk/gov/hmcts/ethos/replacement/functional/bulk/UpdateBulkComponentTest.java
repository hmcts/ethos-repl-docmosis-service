package uk.gov.hmcts.ethos.replacement.functional.bulk;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class UpdateBulkComponentTest {
    private TestUtil testUtil;
    private List<String> caseList = new ArrayList<>();

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    //@WithTag("SmokeTest")
    public void update_bulk_eng_individual_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE3);

        testUtil.executeUpdateBulkTest(false, Constants.TEST_DATA_ENG_BULK1, caseList);
    }

    @Test
    public void update_bulk_eng_company_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE3);

        testUtil.executeUpdateBulkTest(false, Constants.TEST_DATA_ENG_BULK3, caseList);
    }

    @Test
    public void update_bulk_eng_both_individual_and_company_claimants() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE2);

        testUtil.executeUpdateBulkTest(false, Constants.TEST_DATA_ENG_BULK4, caseList);
    }

    @Test
    public void update_bulk_eng_some_cases_invalid() throws IOException {
        testUtil.loadAuthToken();

        String ethosCaseReference = testUtil.getUniqueCaseReference();
        String caseDetails = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1_CASE1), "UTF-8");
        caseDetails = caseDetails.replace("#ETHOS-CASE-REFERENCE#", ethosCaseReference);

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", true, caseDetails);
        Response response = testUtil.getResponse(ccdRequest, Constants.CREATE_CASE_URI);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK5), "UTF-8");
        testData = testData.replace("#ETHOS-CASE-REFERENCE1#", ethosCaseReference);
        ethosCaseReference = testUtil.getUniqueCaseReference();
        testData = testData.replace("#ETHOS-CASE-REFERENCE2#", ethosCaseReference);
        ethosCaseReference = testUtil.getUniqueCaseReference();
        testData = testData.replace("#ETHOS-CASE-REFERENCE3#", ethosCaseReference);

        BulkRequest bulkRequest = testUtil.getBulkRequest(true, testData);
        response = testUtil.getBulkResponse(bulkRequest, Constants.CREATE_BULK_URI);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        response = testUtil.getBulkResponse(bulkRequest, Constants.SEARCH_BULK_URI);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        response = testUtil.getBulkResponse(bulkRequest, Constants.UPDATE_BULK_URI);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

    }

    @Test
    //@WithTag("SmokeTest")
    public void update_bulk_scot_individual_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE3);

        testUtil.executeUpdateBulkTest(false, Constants.TEST_DATA_SCOT_BULK1, caseList);
    }

    @Test
    public void update_bulk_scot_company_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE3);

        testUtil.executeUpdateBulkTest(false, Constants.TEST_DATA_SCOT_BULK2, caseList);
    }

    public void update_bulk_with_no_payload() throws IOException {
        testUtil.loadAuthToken();

        BulkRequest bulkRequest = new BulkRequest();

        Response response = testUtil.getBulkResponse(bulkRequest, Constants.UPDATE_BULK_URI, 500);

    }

    public void update_bulk_with_no_token() throws IOException {

        testUtil.setAuthToken("authToken");

        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1), "UTF-8");
        BulkRequest bulkRequest = testUtil.getBulkRequest(false, testData);

        Response response = testUtil.getBulkResponse(bulkRequest, Constants.UPDATE_BULK_URI, 500);

        testUtil.setAuthToken(null);

    }

    @After
    public void tearDown() {
    }
}
