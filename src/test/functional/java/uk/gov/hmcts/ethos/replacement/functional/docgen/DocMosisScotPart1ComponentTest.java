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
public class DocMosisScotPart1ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_1() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", "REJECTION OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_2() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "", "REJECTION OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "", "ACKNOWLEDGEMENT OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_1() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "1",
                "Your claim has been accepted at this office and the case file has been "
                        + "transferred to the Aberdeen office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_2() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "2",
                "Your claim has been accepted at this office and the case file has been "
                        + "transferred to the Dundee office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "3", "Edinburgh office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_4() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "4",
                "Your claim has been accepted at this office. You should quote the case number shown above on", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_5() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "5", "I am writing to you because your", true);
    }

    @Test
    public void generateDocument_Part_Scot_4() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "", "RECONSIDERATION OF DECISION TO REJECT", true);
    }

    @Test
    public void generateDocument_Part_Scot_5() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "",
                "has reconsidered the decision without a hearing and has", true);
    }

    @Test
    public void generateDocument_Part_Scot_6() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "", "RECONSIDERATION - REJECTION CONFIRMED", true);
    }

    @Test
    public void generateDocument_Part_Scot_7() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "", "NOTICE OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_1() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "1", "NOTICE OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_2() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "2", "Ground Floor Block "
                + "C Caledonian House Greenmarket", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_3() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "3",
                "You should do this by completing and returning the enclosed response form to ETS, "
                        + "54 – 56 Melville Street. Edinburgh EH3 7HF", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_4() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "4", "NOTICE OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_5() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "5", "ACCEPTED OUT OF TIME", true);
    }

    @After
    public void tearDown() throws IOException {
        //testUtil.deleteTempFile();
    }
}
