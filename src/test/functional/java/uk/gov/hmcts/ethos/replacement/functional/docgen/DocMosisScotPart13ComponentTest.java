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
public class DocMosisScotPart13ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_104() throws Exception {
        testUtil.executeGenerateDocumentTest("104", "", "WASTED COSTS ORDER REFUSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_105() throws Exception {
        testUtil.executeGenerateDocumentTest("105", "", "APPLICATION FOR AN EXPENSES ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_105A() throws Exception {
        testUtil.executeGenerateDocumentTest("105", "A", "EXPENSES ORDER/PREPARATION TIME ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_106() throws Exception {
        testUtil.executeGenerateDocumentTest("106", "", "APPLICATION FOR A WASTED COSTS ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_106_A() throws Exception {
        testUtil.executeGenerateDocumentTest("106", "A",
                "An Employment Judge is considering making a wasted costs against you in favour of", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
