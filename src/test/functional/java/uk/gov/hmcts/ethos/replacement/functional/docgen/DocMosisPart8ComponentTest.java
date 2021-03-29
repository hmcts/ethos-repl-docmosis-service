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
public class DocMosisPart8ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part8_1() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "1", "NOTICE OF HEARING");
    }

    @Test
    public void generateDocument_Part8_2() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "2", "NOTICE OF REMEDY HEARING");
    }

    @Test
    public void generateDocument_Part8_3() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "3", "NOTICE OF REMEDY HEARING");
    }

    @Test
    public void generateDocument_Part8_4() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "4", "NOTICE OF COSTS HEARING");
    }

    @Test
    public void generateDocument_Part8_5() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "5", "NOTICE OF WASTED COSTS HEARING");
    }

    @Test
    public void generateDocument_Part8_6() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "6", "NOTICE OF ADJOURNMENT OF HEARING");
    }

    @Test
    public void generateDocument_Part8_7() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "7", "NOTICE OF ADJOURNMENT OF HEARING");
    }

    @Test
    public void generateDocument_Part8_8() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "8", "NOTICE OF POSTPONEMENT OF HEARING");
    }
    
    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
