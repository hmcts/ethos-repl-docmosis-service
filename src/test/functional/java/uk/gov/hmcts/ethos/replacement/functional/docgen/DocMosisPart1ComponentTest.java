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
public class DocMosisPart1ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part1_1() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "1", "ACKNOWLEDGEMENT OF CLAIM");
    }

    @Test
    public void generateDocument_Part1_1A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "1A", "RETURN OF DOCUMENTS");
    }

    @Test
    public void generateDocument_Part1_2() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "2", "ACKNOWLEDGEMENT OF APPLICATION FOR INTERIM RELIEF");
    }

    @Test
    public void generateDocument_Part1_3() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "3", "Employment Tribunals Rules of Procedure 2013");
    }

    @Test
    public void generateDocument_Part1_3A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "3A", "REQUEST NOT TO SEND CLAIM TO RESPONDENT");
    }

    @Test
    public void generateDocument_Part1_3B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "3B", "REDIRECTION OF CLAIM");
    }

    @Test
    public void generateDocument_Part1_4() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "4", "CLAIM NOT ACCEPTED");
    }

    @Test
    public void generateDocument_Part1_5() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "5",
                "I have received your claim form but am unable to accept it because it has not been presented");
    }

    @Test
    public void generateDocument_Part1_6() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "6",
                "I am returning your claim form because you have not given the following minimum information");
    }

    @Test
    public void generateDocument_Part1_6A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "6A",
                "I am returning your claim form because you have not complied with the requirement");
    }

    @Test
    public void generateDocument_Part1_6B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "6B",
                "has been rejected because you have not complied with the requirement");
    }

    @Test
    public void generateDocument_Part1_7() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "7",
                "Your claim form has been referred to Employment Judge");
    }

    @Test
    public void generateDocument_Part1_8() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "8",
                "who has decided that only the following complaints can be accepted");
    }

    @Test
    public void generateDocument_Part1_9() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9",
                "who has decided to reject it because \\[it is in a form which cannot sensibly be responded to\\]");
    }

    @Test
    public void generateDocument_Part1_9A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9A", "Rule 12 \\(1\\)\\(c\\)");
    }

    @Test
    public void generateDocument_Part1_9B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9B", "Rule 12\\(1\\)\\(d\\)");
    }

    @Test
    public void generateDocument_Part1_9C() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9C", "Rule 12\\(1\\)\\(f\\)");
    }

    @Test
    public void generateDocument_Part1_10() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "10",
                "The Judge has also decided that your other complaints should be rejected");
    }

    @Test
    public void generateDocument_Part1_11() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "11", "YOUR QUESTIONS  ANSWERED");
    }

    @Test
    public void generateDocument_Part1_11A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "11A", "CLAIM REJECTION - EARLY CONCILIATION");
    }

    @Test
    public void generateDocument_Part1_12() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "12", "RECONSIDERATION OF DECISION");
    }

    @Test
    public void generateDocument_Part1_13() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "13", "CLAIM ACCEPTED AFTER RECONSIDERATION");
    }

    @Test
    public void generateDocument_Part1_14() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "14", "CLAIM REJECTED - RECONSIDERATION HEARING");
    }

    @Test
    public void generateDocument_Part1_14A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "14A", "CLAIM REJECTED - RECONSIDERATION - DISMISSED");
    }

    @Test
    public void generateDocument_Part1_14B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "14B",
                "Because the original decision to reject that part of your claim was correct");
    }

    @Test
    public void generateDocument_Part1_15() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "15",
                "In your claim form you complain that you were unfairly dismissed.");
    }

    @Test
    public void generateDocument_Part1_16() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "16",
                "In your claim form one of your complaints is that you were unfairly dismissed.");
    }

    @Test
    public void generateDocument_Part1_17() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "17", "The claim is struck out");
    }

    @Test
    public void generateDocument_Part1_18() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "18",
                "The complaint that the claimant was unfairly dismissed is struck out");
    }

    @Test
    public void generateDocument_Part1_19() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "19",
                "has ordered that it be treated as an amendment to the claim");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
