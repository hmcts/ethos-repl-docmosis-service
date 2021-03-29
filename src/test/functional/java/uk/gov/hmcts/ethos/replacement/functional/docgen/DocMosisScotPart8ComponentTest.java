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
public class DocMosisScotPart8ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_64() throws Exception {
        testUtil.executeGenerateDocumentTest("64", "", "WITHDRAWAL OF CLAIM / PART OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_65() throws Exception {
        testUtil.executeGenerateDocumentTest("65", "", "CANCELLATION OF HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_66() throws Exception {
        testUtil.executeGenerateDocumentTest("66", "", "CASE REMAINS LISTED", true);
    }

    @Test
    public void generateDocument_Part_Scot_67() throws Exception {
        testUtil.executeGenerateDocumentTest("67", "",
                "We have received notice from both parties that they have reached an agreement", true);
    }

    @Test
    public void generateDocument_Part_Scot_68() throws Exception {
        testUtil.executeGenerateDocumentTest("68", "",
                "Acas have sent us notice that terms of settlement have been agreed between the parties", true);
    }

    public void generateDocument_Part_Scot_69() throws Exception {
        testUtil.executeGenerateDocumentTest("69", "", "The claim/ complaint of", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
