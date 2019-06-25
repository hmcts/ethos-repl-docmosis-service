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
public class PreDefaultComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void claimant_type_individual_with_england_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", "Individual", false, Constants.TEST_DATA_PRE_DEFAULT1);
    }

    @Test
    public void claimant_type_company_with_england_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", "Individual", false, Constants.TEST_DATA_PRE_DEFAULT2);
    }

    @Test
    @WithTag("SmokeTest")
    public void claimant_type_individual_with_scotland_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", "Individual", true, Constants.TEST_DATA_SCOT_PRE_DEFAULT1);
    }

    @Test
    public void claimant_type_company_with_scotland_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", "Individual", true, Constants.TEST_DATA_SCOT_PRE_DEFAULT1);
    }

    @After
    public void tearDown() {
    }
}
