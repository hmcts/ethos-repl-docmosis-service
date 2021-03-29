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
public class DocMosisPart7ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part7_1() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "1", "It will be heard by an Employment Judge at");
    }

    @Test
    public void generateDocument_Part7_2() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "2", "To take part you should telephone");
    }

    @Test
    public void generateDocument_Part7_3() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "3", "GUIDANCE NOTES FOR PARTIES");
    }

    @Test
    public void generateDocument_Part7_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "4C", "considers that the claimant’s allegations or arguments");
    }

    @Test
    public void generateDocument_Part7_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "4R", "DEPOSIT ORDER");
    }

    @Test
    public void generateDocument_Part7_5() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "5", "NOTE ACCOMPANYING DEPOSIT ORDER");
    }

    @Test
    public void generateDocument_Part7_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "6C", "The Order was sent to the claimant on");
    }

    @Test
    public void generateDocument_Part7_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "6R", "The response is struck out");
    }

    @Test
    public void generateDocument_Part7_7() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "7", "The hearing will take place at");
    }

    @Test
    public void generateDocument_Part7_8() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "8", "CASE MANAGEMENT – BY TELEPHONE");
    }

    @Test
    public void generateDocument_Part7_9() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "9",
                "Please ensure that you attend so that the discussion can start on time");
    }

    @Test
    public void generateDocument_Part7_10() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "10", "NOTICE OF PRELIMINARY HEARING");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
