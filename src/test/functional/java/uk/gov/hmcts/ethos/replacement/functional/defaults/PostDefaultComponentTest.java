package uk.gov.hmcts.ethos.replacement.functional.defaults;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
public class PostDefaultComponentTest {
    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void claimant_individual_with_england_template() throws IOException {
        executeTest(Constants.TEST_DATA_POST_DEFAULT1, false);
    }

    @Test
    public void claimant_company_with_england_template() throws IOException {
        executeTest(Constants.TEST_DATA_POST_DEFAULT2, false);
    }

    @Test
    public void claimant_individual_represented_with_england_template() throws IOException {
        executeTest(Constants.TEST_DATA_POST_DEFAULT3, false);
    }

    @Test
    public void respondent_represented_with_england_template() throws IOException {
        executeTest(Constants.TEST_DATA_POST_DEFAULT4, false);
    }

    @Test
    @WithTag("SmokeTest")
    public void claimant_individual_with_scotland_template() throws IOException {
        executeTest(Constants.TEST_DATA_SCOT_POST_DEFAULT1, true);
    }

    @Test
    public void claimant_company_with_scotland_template() throws IOException {
        executeTest(Constants.TEST_DATA_SCOT_POST_DEFAULT2, true);
    }

    @After
    public void tearDown() {
    }

    private void executeTest(String testData, boolean isScotland) throws IOException {
        testUtil.executePostDefaultValuesTest("positionType", "Awaiting ET3", false, testData);
        if (isScotland) {
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceAddress", "Eagle Building, 215 Bothwell Street, Glasgow, G2 7TS", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceTelephone", "0141 204 0730", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceFax", "01264 785 177", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceDX", "DX 580003", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceEmail", "glasgowet@justice.gov.uk", false, testData);
        } else {
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceAddress", "Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceTelephone", "0161 833 6100", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceFax", "0870 739 4433", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceDX", "DX 743570", false, testData);
            testUtil.executePostDefaultValuesTest("tribunalCorrespondenceEmail", "Manchesteret@justice.gov.uk", false, testData);
        }
    }
}
