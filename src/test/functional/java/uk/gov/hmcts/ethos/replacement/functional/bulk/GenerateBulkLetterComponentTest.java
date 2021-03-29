package uk.gov.hmcts.ethos.replacement.functional.bulk;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
@Ignore
public class GenerateBulkLetterComponentTest {
    private TestUtil testUtil;
    private List<String> caseList = new ArrayList<>();

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void generate_bulk_letter_eng_individual_claimant_not_represented() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE3);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", false, Constants.TEST_DATA_ENG_BULK1, caseList);

    }

    @Test
    public void generate_bulk_letter_eng_individual_claimant_represented() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK2_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK2_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK2_CASE3);

        testUtil.executeGenerateBulkLetterTest("1", "1", "ACKNOWLEDGEMENT OF CLAIM",
                false, Constants.TEST_DATA_ENG_BULK2, caseList);

    }

    @Test
    public void generate_bulk_letter_eng_company_claimant_not_represented() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE3);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", false, Constants.TEST_DATA_ENG_BULK3, caseList);

    }

    @Test
    public void generate_bulk_letter_eng_both_individual_and_company_claimants() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_ENG_BULK3_CASE2);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", false, Constants.TEST_DATA_ENG_BULK4, caseList);
    }

    @Test
    public void generate_bulk_letter_eng_no_cases() throws Exception {
        caseList.clear();
        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", false, Constants.TEST_DATA_ENG_BULK4, caseList);
    }

    @Test
    public void generate_bulk_letter_scot_individual_claimant_not_represented() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE3);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", true, Constants.TEST_DATA_SCOT_BULK1, caseList);

    }

    @Test
    public void generate_bulk_letter_scot_individual_claimant_represented() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK2_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK2_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK2_CASE3);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", true, Constants.TEST_DATA_SCOT_BULK2, caseList);

    }

    @Test
    public void generate_bulk_letter_scot_company_claimant_not_represented() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE3);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", true, Constants.TEST_DATA_SCOT_BULK3, caseList);

    }

    @Test
    public void generate_bulk_letter_scot_both_individual_and_company_claimants() throws Exception {
        caseList.clear();
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK1_CASE2);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE1);
        caseList.add(Constants.TEST_DATA_SCOT_BULK3_CASE2);

        testUtil.executeGenerateBulkLetterTest("1", "1",
                "ACKNOWLEDGEMENT OF CLAIM", true, Constants.TEST_DATA_SCOT_BULK4, caseList);

    }

    @Test
    public void generate_bulk_letter_with_no_payload() throws Exception {
        testUtil.loadAuthToken();

        BulkRequest bulkRequest = new BulkRequest();

        Response response = testUtil.getBulkResponse(bulkRequest,
                Constants.GENERATE_BULK_LETTER_URI, 500);

    }

    @Test
    public void generate_bulk_letter_with_invalid_token() throws Exception {
        testUtil.setAuthToken("authToken");

        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1), "UTF-8");
        BulkRequest bulkRequest = testUtil.getBulkRequest(false, testData);

        Response response = testUtil.getBulkResponse(bulkRequest,
                Constants.GENERATE_BULK_LETTER_URI, 500);

        testUtil.setAuthToken(null);

    }

    @After
    public void tearDown() {
    }
}
