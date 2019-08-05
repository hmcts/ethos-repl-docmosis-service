package uk.gov.hmcts.ethos.replacement.functional.bulk;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class CreateBulkComponentTest {

    private TestUtil testUtil;
    private List<String> caseList = new ArrayList<>();

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void create_bulk_eng_individual_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE3);

        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK1, caseList);
    }

    public void create_bulk_eng_individual_claimant_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK2_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK2_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK2_CASE3);

        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK2, caseList);
    }

    public void create_bulk_eng_company_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE3);

        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK3, caseList);
    }

    public void create_bulk_eng_both_individual_and_company_claimants() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE2);

        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK4, caseList);
    }

    public void create_bulk_eng_some_cases_invalid() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK5_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK5_CASE2);

        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK5, caseList);
    }

    public void create_bulk_eng_all_cases_invalid() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK5_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK5_CASE2);

        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK6, caseList);
    }

    @Test
    @WithTag("SmokeTest")
    public void create_bulk_scot_individual_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE3);

        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK1, caseList);
    }

    public void create_bulk_scot_individual_claimant_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK2_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK2_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK2_CASE3);

        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK2, caseList);
    }

    public void create_bulk_scot_company_claimant_not_represented() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE3);

        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK3, caseList);
    }

    public void create_bulk_scot_both_individual_and_company_claimants() throws IOException {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE2);

        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK4, caseList);
    }

    public void create_bulk_with_no_payload() throws IOException {
        testUtil.loadAuthToken();

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", false, "");

        Response response = testUtil.getResponse(ccdRequest, Constants.CREATE_BULK_URI, 400);

    }

    public void create_bulk_with_no_token() throws IOException {

        testUtil.loadAuthToken();

        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "", false, Constants.TEST_DATA_ENG_BULK1);

        Response response = testUtil.getResponse(ccdRequest, Constants.CREATE_BULK_URI, 401);

    }

    @After
    public void tearDown() {
    }
}
