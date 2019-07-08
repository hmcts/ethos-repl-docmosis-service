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
public class DocMosisPart18ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part18_1() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "1", "ANNEX TO THE JUDGMENT");
    }

    @Test
    public void generateDocument_Part18_2() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "2", "ANNEX TO THE JUDGMENT");
    }

    @Test
    public void generateDocument_Part18_3() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "3", "NOTIFICATION OF RECOUPMENT");
    }

    @Test
    public void generateDocument_Part18_4() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "4", "NOTIFICATION OF RECOUPMENT - PROTECTIVE AWARD");
    }

    @Test
    public void generateDocument_Part18_5() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "5", "INTEREST ON DISCRIMINATION AND EQUAL PAY AWARDS");
    }

    @Test
    public void generateDocument_Part18_6() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "6", "INTEREST ON TRIBUNAL AWARDS");
    }

    @Test
    public void generateDocument_Part18_7() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "7", "INTEREST ON TRIBUNAL AWARDS");
    }

    @Test
    public void generateDocument_Part18_8() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "8", "INTEREST ON TRIBUNAL AWARDS");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
