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
public class DocMosisPart13ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part13_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "1C", "This case will be listed for a hearing in");
    }

    @Test
    public void generateDocument_Part13_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "1R", "This case will be listed for a hearing in");
    }

    @Test
    public void generateDocument_Part13_2() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "2",
                "I have been informed that a settlement agreement has been made between the parties");
    }

    @Test
    public void generateDocument_Part13_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "2C", "which has been placed on the file");
    }

    @Test
    public void generateDocument_Part13_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "2R", "which has been placed on the file");
    }

    @Test
    public void generateDocument_Part13_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "3C", "Thank you for your letter dated");
    }

    @Test
    public void generateDocument_Part13_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "3R", "Thank you for your letter dated");
    }

    @Test
    public void generateDocument_Part13_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "4C", "Please let me have your reply in writing by");
    }

    @Test
    public void generateDocument_Part13_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "4R", "Please let me have your reply in writing by");
    }

    @Test
    public void generateDocument_Part13_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "5C", "has asked for your comments on the enclosed letter");
    }

    @Test
    public void generateDocument_Part13_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "5R", "has asked for your comments on the enclosed letter");
    }

    @Test
    public void generateDocument_Part13_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "6C",
                "has directed that the parties are to write to the Tribunal");
    }

    @Test
    public void generateDocument_Part13_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "6R",
                "has directed that the parties are to write to the Tribunal");
    }

    @Test
    public void generateDocument_Part13_7C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "7C",
                "Your letter has been treated as a change of address for correspondence");
    }

    @Test
    public void generateDocument_Part13_7R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "7R",
                "Your letter has been treated as a change of address for correspondence");
    }

    @Test
    public void generateDocument_Part13_8C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "8C",
                "Please note that correspondence intended for the Employment Tribunal should be addressed to");
    }

    @Test
    public void generateDocument_Part13_8R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "8R",
                "Please note that correspondence intended for the Employment Tribunal should be addressed to");
    }

    @Test
    public void generateDocument_Part13_9C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "9C",
                "has asked me to explain that correspondence between the parties");
    }

    @Test
    public void generateDocument_Part13_9R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "9R",
                "has asked me to explain that correspondence between the parties");
    }

    @Test
    public void generateDocument_Part13_10C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "10C", "REQUEST FOR ADVICE");
    }

    @Test
    public void generateDocument_Part13_10R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "10R", "REQUEST FOR ADVICE");
    }

    @Test
    public void generateDocument_Part13_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "11C", "REQUEST TO TRANSFER CASE");
    }

    @Test
    public void generateDocument_Part13_11R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "11R", "REQUEST TO TRANSFER CASE");
    }

    @Test
    public void generateDocument_Part13_12C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "12C", "REJECTION OF TRANSFER REQUEST");
    }

    @Test
    public void generateDocument_Part13_12R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "12R", "REJECTION OF TRANSFER REQUEST");
    }

    @Test
    public void generateDocument_Part13_13() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "13", "TRANSFER OF CASE");
    }

    @Test
    public void generateDocument_Part13_14C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "14C", "REFUSAL OF A TRANSFER \\(COUNTRY TO COUNTRY\\)");
    }

    @Test
    public void generateDocument_Part13_14R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "14R", "REFUSAL OF A TRANSFER \\(COUNTRY TO COUNTRY\\)");
    }

    @Test
    public void generateDocument_Part13_15() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "15", "ENFORCEMENT OF AWARD");
    }

    @Test
    public void generateDocument_Part13_16() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "16",
                "EMPLOYMENT TRIBUNALS ACT 1996 section 4\\(3\\)\\(e\\)");
    }

    @Test
    public void generateDocument_Part13_17() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "17", "EMPLOYMENT TRIBUNALS ACT 1996 section");
    }

    @Test
    public void generateDocument_Part13_18() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "18", "AUTHORISATION TO ACT IN PROCEEDINGS");
    }

    @Test
    public void generateDocument_Part13_19() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "19", "SETTLEMENT ENQUIRY");
    }

    @Test
    public void generateDocument_Part13_20() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "20", "SETTLEMENT OF CLAIM");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
