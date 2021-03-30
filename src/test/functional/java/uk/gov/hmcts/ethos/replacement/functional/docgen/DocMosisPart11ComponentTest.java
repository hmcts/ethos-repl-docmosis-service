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
public class DocMosisPart11ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part11_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "1C", "RECONSIDERATION OF JUDGMENT: REFUSAL");
    }

    @Test
    public void generateDocument_Part11_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "1R", "RECONSIDERATION OF JUDGMENT: REFUSAL");
    }

    @Test
    public void generateDocument_Part11_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "2C",
                "EXTENSION OF TIME GRANTED FOR RECONSIDERATION OF JUDGMENT");
    }

    @Test
    public void generateDocument_Part11_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "2R",
                "EXTENSION OF TIME GRANTED FOR RECONSIDERATION OF JUDGMENT");
    }

    @Test
    public void generateDocument_Part11_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "3C", "RECONSIDERATION OF JUDGMENT: REJECTED");
    }

    @Test
    public void generateDocument_Part11_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "3R", "RECONSIDERATION OF JUDGMENT: REJECTED");
    }

    @Test
    public void generateDocument_Part11_4() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "4", "The grounds for the proposed reconsideration are that");
    }

    @Test
    public void generateDocument_Part11_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "5C",
                "setting out their views on whether the application can be determined without a hearing");
    }

    @Test
    public void generateDocument_Part11_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "5R",
                "setting out their views on whether the application can be determined without a hearing");
    }

    @Test
    public void generateDocument_Part11_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "6C", "The claimant’s application dated");
    }

    @Test
    public void generateDocument_Part11_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "6R", "The respondent’s application dated");
    }

    @Test
    public void generateDocument_Part11_7() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "7",
                "the interests of justice do not require a hearing, and the judgment dated");
    }

    @Test
    public void generateDocument_Part11_8() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "8",
                "If it is revoked, the case will be adjourned to be re-heard on its merits on a date");
    }

    @Test
    public void generateDocument_Part11_9() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "9",
                "If it is revoked, the re-hearing of the case will follow immediately");
    }

    @Test
    public void generateDocument_Part11_10() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "10",
                "under rule 71 of the Employment Tribunals Rules of Procedure 2013");
    }

    @Test
    public void generateDocument_Part11_11() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "11", "to reconsider the judgment under");
    }

    @Test
    public void generateDocument_Part11_12() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "12",
                "under rule 71 of the Employment Tribunals Rules of Procedure 2013, and without a hearing");
    }

    @Test
    public void generateDocument_Part11_13() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "13", "to reconsider the judgment under");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
