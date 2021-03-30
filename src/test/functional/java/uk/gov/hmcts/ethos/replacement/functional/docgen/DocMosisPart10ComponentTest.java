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
public class DocMosisPart10ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part10_1() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "1",
                "Written reasons will not be provided unless a written request is presented by either party within "
                        + "14 days of the sending of this written record of the decision");
    }

    @Test
    public void generateDocument_Part10_2() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "2",
                "Written reasons will not be provided unless a written request is presented by either party within "
                        + "14 days of the sending of this written record of the decision");
    }

    @Test
    public void generateDocument_Part10_3() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "3", "RESERVED REASONS");
    }

    @Test
    public void generateDocument_Part10_4() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "4",
                "The judgment in this case was reserved and will be sent to the parties in writing");
    }

    @Test
    public void generateDocument_Part10_5() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "5", "RESERVED JUDGMENT");
    }

    @Test
    public void generateDocument_Part10_6() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "6", "EMPLOYMENT TRIBUNAL JUDGMENT");
    }

    @Test
    public void generateDocument_Part10_7() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "7", "JUDGMENT");
    }

    @Test
    public void generateDocument_Part10_8() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "8",
                "having been sent to the parties on");
    }

    @Test
    public void generateDocument_Part10_9() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "9",
                "Although it is out of time the Judge considers that it is in the interests of justice to "
                        + "extend the time limit for the application, which has been granted");
    }

    @Test
    public void generateDocument_Part10_10() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "10",
                "Although it is out of time the Judge considers that it is in the interests of justice to "
                        + "extend the time limit for the application, which is granted");
    }

    @Test
    public void generateDocument_Part10_11() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "11",
                "REFUSAL OF LATE REQUEST FOR WRITTEN REASONS");
    }

    @Test
    public void generateDocument_Part10_12() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "12",
                "REFUSAL OF LATE REQUEST FOR WRITTEN REASONS");
    }

    @Test
    public void generateDocument_Part10_13() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "13", "BY CONSENT");
    }

    @Test
    public void generateDocument_Part10_14() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "14", "CONSENT ORDER");
    }

    @Test
    public void generateDocument_Part10_15() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "15", "CERTIFICATE OF CORRECTION");
    }

    @Test
    public void generateDocument_Part10_16() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "16", "CORRECTED JUDGMENT");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
