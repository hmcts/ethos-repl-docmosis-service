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
public class DocMosisPart3ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part3_1() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "1", "EMPLOYER’S CONTRACT CLAIM REJECTED");
    }

    @Test
    public void generateDocument_Part3_2() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "2", "Rule 23 provides that an employer’s contract claim");
    }

    @Test
    public void generateDocument_Part3_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "3", "The remainder of your response has been accepted");
    }

    @Test
    public void generateDocument_Part3_4() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "4", "who has decided that only the following parts can be accepted");
    }

    @Test
    public void generateDocument_Part3_5() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "5", "RECONSIDERATION OF DECISION");
    }

    @Test
    public void generateDocument_Part3_6() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "6", "EMPLOYER’S CONTRACT CLAIM ACCEPTED AFTER RECONSIDERATION");
    }

    @Test
    public void generateDocument_Part3_7() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "7", "EMPLOYER’S CONTRACT CLAIM REJECTED - RECONSIDERATION HEARING");
    }

    @Test
    public void generateDocument_Part3_8() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "8", "employer’s contract claim against you by the");
    }

    @Test
    public void generateDocument_Part3_9() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "9", "to the employer’s contract claim at this stage");
    }

    @Test
    public void generateDocument_Part3_10() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "10", "RESPONSE REQUIRED TO PART");
    }

    @Test
    public void generateDocument_Part3_11() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "11", "I refer to the tribunal’s letter of");
    }

    @Test
    public void generateDocument_Part3_12() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "12", "APPLICATION FOR EXTENSION OF TIME TO SUBMIT RESPONSE");
    }

    @Test
    public void generateDocument_Part3_13() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "13", "EMPLOYER’S CONTRACT CLAIM GRANTED");
    }

    @Test
    public void generateDocument_Part3_14() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "14", "EMPLOYER’S CONTRACT CLAIM REFUSED");
    }

    @Test
    public void generateDocument_Part3_15() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "15", "RESPONSE TO EMPLOYER’S CONTRACT");
    }

    @Test
    public void generateDocument_Part3_16() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "16", "RESPONSE TO EMPLOYER’S CONTRACT CLAIM SUBMITTED LATE");
    }

    @Test
    public void generateDocument_Part3_17() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "17", "APPLICATION FOR EXTENSION OF TIME TO PRESENT RESPONSE");
    }

    @Test
    public void generateDocument_Part3_18() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "18", "NO RESPONSE TO EMPLOYER’S CONTRACT CLAIM RECEIVED");
    }

    @Test
    public void generateDocument_Part3_19() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "19",
                "has ordered that it be treated as an amendment to the response to the employer’s contract claim");
    }

    @Test
    public void generateDocument_Part3_20() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "20", "RESPONSE SUBMITTED LATE");
    }

    @Test
    public void generateDocument_Part3_21() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "21", "RESPONSE ACCEPTED");
    }

    @Test
    public void generateDocument_Part3_22() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "22", "REJECTION OF RESPONSE");
    }

    @Test
    public void generateDocument_Part3_23() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "23", "Why has the response been rejected");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
