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
public class DocMosisScotPart5ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_26() throws Exception {
        testUtil.executeGenerateDocumentTest("26", "",
                "INITIAL CONSIDERATION OF CLAIM AND RESPONSE", true);
    }

    @Test
    public void generateDocument_Part_Scot_27() throws Exception {
        testUtil.executeGenerateDocumentTest("27", "", "and any further information", true);
    }

    @Test
    public void generateDocument_Part_Scot_28() throws Exception {
        testUtil.executeGenerateDocumentTest("28", "", "and any further information provided", true);
    }

    @Test
    public void generateDocument_Part_Scot_29() throws Exception {
        testUtil.executeGenerateDocumentTest("29", "", "DISMISSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_30() throws Exception {
        testUtil.executeGenerateDocumentTest("30", "", "DISMISSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_31() throws Exception {
        testUtil.executeGenerateDocumentTest("31", "", "ALLOWED TO PROCEED", true);
    }

    @Test
    public void generateDocument_Part_Scot_32() throws Exception {
        testUtil.executeGenerateDocumentTest("32", "", "ALLOWED TO PROCEED", true);
    }

    @Test
    public void generateDocument_Part_Scot_33() throws Exception {
        testUtil.executeGenerateDocumentTest("33", "",
                "INITIAL CONSIDERATION OF CLAIM AND RESPONSE completed", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
