package uk.gov.hmcts.ethos.replacement.docmosis.test.docgen;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.docmosis.test.ComponentTest;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.Constants;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.TestUtil;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest")
})
public class DocMosisComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void verify_payload_eng_claimant_individual_not_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "1", false, Constants.TEST_DATA_CASE1);
    }

    @Test
    public void verify_payload_eng_claimant_company_not_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "1", false, Constants.TEST_DATA_CASE2);
    }

    @Test
    public void verify_payload_eng_claimant_individual_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "1", false, Constants.TEST_DATA_CASE3);
    }

    @Test
    public void verify_payload_eng_respondant_represented() throws Exception {
        testUtil.verifyDocMosisPayload("1", "1", false, Constants.TEST_DATA_CASE4);
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

    @After
    public void tearDown() {

    }
}
