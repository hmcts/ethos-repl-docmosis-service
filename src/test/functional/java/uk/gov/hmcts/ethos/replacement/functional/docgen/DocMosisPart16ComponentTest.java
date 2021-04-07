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
public class DocMosisPart16ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part16_1() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "1",
                "INCORPORATING NOTICE OF STAGE 1 EQUAL VALUE HEARING");
    }

    @Test
    public void generateDocument_Part16_2() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "2",
                "INCORPORATING NOTICE OF STAGE 2 EQUAL VALUE HEARING");
    }

    @Test
    public void generateDocument_Part16_3() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "3",
                "A member of the panel of independent experts designated by");
    }

    @Test
    public void generateDocument_Part16_4() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "4", "REQUIREMENT TO EXPERT TO");
    }

    @Test
    public void generateDocument_Part16_5() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "5", "RECEIPT OF INDEPENDENT EXPERT’S REPORT");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
