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
public class DocMosisPart6ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part6_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "1C", "STRIKE OUT WARNING");
    }

    @Test
    public void generateDocument_Part6_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "1R", "STRIKE OUT WARNING");
    }

    @Test
    public void generateDocument_Part6_2() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "2", "The claim is struck out");
    }

    @Test
    public void generateDocument_Part6_3() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "3",
                "the Tribunal gave the claimant an opportunity to make "
                        + "representations or to request a hearing, as to why the the complaint of");
    }

    @Test
    public void generateDocument_Part6_4() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "4", "The response is struck out\\.");
    }

    @Test
    public void generateDocument_Part6_5() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "5", "The response is struck out in part");
    }

    @Test
    public void generateDocument_Part6_6() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "6",
                "Thank you for informing the Tribunal that you have withdrawn your claim");
    }

    @Test
    public void generateDocument_Part6_7() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "7", "WITHDRAWAL OF PART OF CLAIM");
    }

    @Test
    public void generateDocument_Part6_8() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "8",
                "The proceedings are dismissed following a withdrawal of the claim by the claimant");
    }

    @Test
    public void generateDocument_Part6_9() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "9", "dismissed following a withdrawal by the claimant");
    }

    @Test
    public void generateDocument_Part6_10() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "10",
                "has directed that a judgment dismissing the claim is not issued");
    }

    @Test
    public void generateDocument_Part6_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "11C", "the hearing will be cancelled");
    }

    @Test
    public void generateDocument_Part6_11R() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "11R", "the hearing will be cancelled");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
