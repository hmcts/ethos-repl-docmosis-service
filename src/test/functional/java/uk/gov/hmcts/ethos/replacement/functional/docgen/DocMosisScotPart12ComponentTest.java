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
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.IOException;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class DocMosisScotPart12ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_99() throws Exception {
        testUtil.executeGenerateDocumentTest("99", "", "RECONSIDERATION OF JUDGMENT - REJECTION", true);
    }

    @Test
    public void generateDocument_Part_Scot_100() throws Exception {
        testUtil.executeGenerateDocumentTest("100", "", "APPLICATION FOR RECONSIDERATION OF JUDGMENT REFUSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_101() throws Exception {
        testUtil.executeGenerateDocumentTest("101", "", "requesting the Tribunal’s judgment be reconsidered", true);
    }

    @Test
    public void generateDocument_Part_Scot_102() throws Exception {
        testUtil.executeGenerateDocumentTest("102", "", "RECONSIDERATION OF JUDGMENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_103() throws Exception {
        testUtil.executeGenerateDocumentTest("103", "", "RECONSIDERATION OF JUDGMENT WITHOUT A HEARING", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
