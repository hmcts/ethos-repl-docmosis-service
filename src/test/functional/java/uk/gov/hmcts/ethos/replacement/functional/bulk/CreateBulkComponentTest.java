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

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class CreateBulkComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void create_bulk_eng_individual_claimant_not_represented() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK1);
    }

    public void create_bulk_eng_individual_claimant_represented() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK2);
    }

    public void create_bulk_eng_company_claimant_not_represented() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK3);
    }

    public void create_bulk_eng_both_individual_and_company_claimants() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK5);
    }

    public void create_bulk_eng_some_cases_invalid() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK6);
    }

    public void create_bulk_eng_all_cases_invalid() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_ENG_BULK7);
    }

    @Test
    @WithTag("SmokeTest")
    public void create_bulk_scot_individual_claimant_not_represented() throws IOException {
        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK1);
    }

    public void create_bulk_scot_individual_claimant_represented() throws IOException {
        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK2);
    }

    public void create_bulk_scot_company_claimant_not_represented() throws IOException {
        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK3);
    }

    public void create_bulk_scot_both_individual_and_company_claimants() throws IOException {
        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_SCOT_BULK5);
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
