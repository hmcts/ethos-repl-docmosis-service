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
public class DocMosisPart12ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part12_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "1C",
                "Please see the enclosed letter which contains an application for a costs order "
                        + "to be made against you");
    }

    @Test
    public void generateDocument_Part12_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "1R",
                "Please see the enclosed letter which contains an application for a costs order "
                        + "to be made against you");
    }

    @Test
    public void generateDocument_Part12_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "2C",
                "Your application for a costs order cannot be accepted as it was received more than 28 days");
    }

    @Test
    public void generateDocument_Part12_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "2R",
                "Your application for a costs order cannot be accepted as it was received more than 28 days");
    }

    @Test
    public void generateDocument_Part12_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "3C",
                "Please see the enclosed letter which contains an application for a preparation time order to be");
    }

    @Test
    public void generateDocument_Part12_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "3R",
                "Please see the enclosed letter which contains an application for a preparation time order to be");
    }

    @Test
    public void generateDocument_Part12_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "4C",
                "Your application for a preparation time order cannot be accepted as it was received more than");
    }

    @Test
    public void generateDocument_Part12_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "4R",
                "Your application for a preparation time order cannot be accepted as it was received more than");
    }

    @Test
    public void generateDocument_Part12_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "5C",
                "Please see the enclosed letter which contains an application for a wasted costs order to be");
    }

    @Test
    public void generateDocument_Part12_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "5R",
                "Please see the enclosed letter which contains an application for a wasted costs order to be");
    }

    @Test
    public void generateDocument_Part12_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "6C", "CONSIDERATION OF A WASTED COSTS ORDER");
    }

    @Test
    public void generateDocument_Part12_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "6R", "CONSIDERATION OF A WASTED COSTS ORDER");
    }

    @Test
    public void generateDocument_Part12_7C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "7C", "WASTED COSTS ORDER: APPLICATION REFUSED");
    }

    @Test
    public void generateDocument_Part12_7R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "7R", "WASTED COSTS ORDER: APPLICATION REFUSED");
    }

    @Test
    public void generateDocument_Part12_8C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "8C", "WASTED COSTS ORDER APPLICATION REFUSED");
    }

    @Test
    public void generateDocument_Part12_8R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "8R", "WASTED COSTS ORDER APPLICATION REFUSED");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
