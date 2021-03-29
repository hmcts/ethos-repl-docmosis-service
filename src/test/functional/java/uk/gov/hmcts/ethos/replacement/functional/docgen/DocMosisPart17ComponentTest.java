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
public class DocMosisPart17ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part17_1() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "1", "ACKNOWLEDGEMENT OF APPEAL");
    }

    @Test
    public void generateDocument_Part17_2() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "2",
                "The Tribunal office has received a Notice of Appeal against");
    }

    @Test
    public void generateDocument_Part17_3() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "3", "NOTICE OF APPEAL HEARING");
    }

    @Test
    public void generateDocument_Part17_4() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "4",
                "HEALTH & SAFETY AT WORK ETC ACT 1974: NOTICE OF APPEAL");
    }

    @Test
    public void generateDocument_Part17_5() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "5",
                "IN THE MATTER OF the Employment Tribunals Rules of Procedure 2013");
    }

    @Test
    public void generateDocument_Part17_6() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "6", "NOTES ON TRIBUNAL JUDGMENTS");
    }

    @Test
    public void generateDocument_Part17_7() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "7", "ACKNOWLEDGMENT OF WITHDRAWAL OF APPEAL");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
