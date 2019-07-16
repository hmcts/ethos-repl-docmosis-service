package uk.gov.hmcts.ethos.replacement.functional.bulk;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
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
public class CreateBulkComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void create_bulk_eng_test1() throws IOException {
        testUtil.executeCreateBulkTest(false, Constants.TEST_DATA_BULK1);
    }

    @Test
    @WithTag("SmokeTest")
    public void create_bulk_scot_test1() throws IOException {
        testUtil.executeCreateBulkTest(true, Constants.TEST_DATA_BULK1);
    }


    @After
    public void tearDown() {
    }
}
