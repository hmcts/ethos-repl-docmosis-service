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
public class DocMosisPart4ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part4_1() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "1", "ORDER TO PROVIDE FURTHER INFORMATION");
    }

    @Test
    public void generateDocument_Part4_2() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "2", "Rule 27\\(1\\) – Initial Consideration");
    }

    @Test
    public void generateDocument_Part4_3() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "3", "Rule 27\\(1\\) – Initial Consideration");
    }

    @Test
    public void generateDocument_Part4_4() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "4", "CONFIRMATION OF DISMISSAL OF CLAIM");
    }

    @Test
    public void generateDocument_Part4_5() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "5", "If any part of the claim is permitted to proceed");
    }

    @Test
    public void generateDocument_Part4_6() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "6",
                "a judgment may be issued and the respondent will only be entitled to participate in any hearing");
    }

    @Test
    public void generateDocument_Part4_7() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "7",
                "a judgment may be issued in respect of that part and the respondent will only be");
    }

    @Test
    public void generateDocument_Part4_8() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "8", "CONFIRMATION OF DISMISSAL OF RESPONSE");
    }

    @Test
    public void generateDocument_Part4_8A() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "8A", "Rule 28\\(3\\) Initial Consideration");
    }

    @Test
    public void generateDocument_Part4_9() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "9",
                "You should provide the full and correct name of your former employer");
    }

    @Test
    public void generateDocument_Part4_9A() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "9A", "RESPONDENT’S NAME ON RESPONSE");
    }

    @Test
    public void generateDocument_Part4_10() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "10", "has decided to issue a judgment, a copy of which");
    }

    @Test
    public void generateDocument_Part4_11() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "11",
                "Your application for a reconsideration of the judgment has been accepted");
    }

    @Test
    public void generateDocument_Part4_12() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "12",
                "RECONSIDERATION OF RULE 21 JUDGMENT -  EXTENSION OF TIME GRANTED");
    }

    @Test
    public void generateDocument_Part4_13() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "13", "RECONSIDERATION OF RULE 21 JUDGMENT REJECTED");
    }

    @Test
    public void generateDocument_Part4_14() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "14", "RECONSIDERATION OF RULE 21 JUDGMENT DENIED");
    }

    @Test
    public void generateDocument_Part4_15() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "15", "RULE 21 JUDGMENT – CLAIM NOT QUANTIFIED");
    }

    @Test
    public void generateDocument_Part4_16() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "16", "RULE 21 JUDGMENT NOT APPROPRIATE");
    }

    @Test
    public void generateDocument_Part4_17() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "17",
                "The respondent has made an unauthorised deduction from the claimant's wages");
    }

    @Test
    public void generateDocument_Part4_18() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "18", "Initial Consideration – Rule 26 Referral");
    }

    @Test
    public void generateDocument_Part4_19() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "19", "Rule 21 Referral");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
