package uk.gov.hmcts.ethos.replacement.functional.docgen;

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
public class DocMosisScotLocationsComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void generateDocument_Scot_Glasgow_Location() throws Exception {
        testUtil.executeOutstationDocumentTest("1", "", "glasgowet@justice.gov.uk", true,
                Constants.TEST_DATA_SCOT_GLASGOW_CASE1);
    }

    @Test
    public void generateDocument_Scot_Aberdeen_Location() throws Exception {
        testUtil.executeOutstationDocumentTest("1", "", "aberdeenet@justice.gov.uk", true,
                Constants.TEST_DATA_SCOT_ABERDEEN_CASE1);
    }

    @Test
    public void generateDocument_Scot_Dundee_Location() throws Exception {
        testUtil.executeOutstationDocumentTest("1", "", "dundeeet@justice.gov.uk", true,
                Constants.TEST_DATA_SCOT_DUNDEE_CASE1);
    }

    @Test
    public void generateDocument_Scot_Edinburgh_Location() throws Exception {
        testUtil.executeOutstationDocumentTest("1", "", "edinburghet@justice.gov.uk", true,
                Constants.TEST_DATA_SCOT_EDINBURGH_CASE1);
    }

    @After
    public void tearDown() throws IOException {
        //testUtil.deleteTempFile();
    }
}
