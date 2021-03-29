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
public class DocMosisScotPart4ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_18() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "", "ACKNOWLEDGMENT OF EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_19() throws Exception {
        testUtil.executeGenerateDocumentTest("19", "", "Rejection of EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_19_A() throws Exception {
        testUtil.executeGenerateDocumentTest("19", "A",
                "RECONSIDERATION OF DECISION TO REJECT EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_20() throws Exception {
        testUtil.executeGenerateDocumentTest("20", "", "NOTICE OF EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_21() throws Exception {
        testUtil.executeGenerateDocumentTest("21", "", "ACKNOWLEDGMENT OF RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_22() throws Exception {
        testUtil.executeGenerateDocumentTest("22", "", "NOTICE OF RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_23() throws Exception {
        testUtil.executeGenerateDocumentTest("23", "", "REJECTION OF RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_24() throws Exception {
        testUtil.executeGenerateDocumentTest("24", "", "EXTENSION "
                + "OF TIME FOR RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_25() throws Exception {
        testUtil.executeGenerateDocumentTest("25", "", "EXTENSION "
                + "OF TIME FOR RESPONSE TO CONTRACT CLAIM REFUSED", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
