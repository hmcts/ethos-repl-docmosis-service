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
public class DocMosisScotPart16ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_180() throws Exception {
        testUtil.executeGenerateDocumentTest("180", "", "RECEIPT FOR APPLICATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_181() throws Exception {
        testUtil.executeGenerateDocumentTest("181", "", "NOTICE OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_182() throws Exception {
        testUtil.executeGenerateDocumentTest("182", "", "NOTICE OF HEARING – INTERIM RELIEF", true);
    }

    @Test
    public void generateDocument_Part_Scot_183() throws Exception {
        testUtil.executeGenerateDocumentTest("183", "", "ACKNOWLEDGMENT OF APPLICATION FOR "
                + "DIRECTION SUSPENDING", true);
    }

    @Test
    public void generateDocument_Part_Scot_184() throws Exception {
        testUtil.executeGenerateDocumentTest("184", "",
                "The Tribunal has received an application by the appellant under section 24\\(3\\)\\(b\\) "
                        + "of the Health", true);
    }

    @Test
    public void generateDocument_Part_Scot_185() throws Exception {
        testUtil.executeGenerateDocumentTest("185", "",
                "Upon the appellant’s application under section 24\\(3\\)\\(b\\) of the Health and Safety "
                        + "at Work etc", true);
    }

    @Test
    public void generateDocument_Part_Scot_186() throws Exception {
        testUtil.executeGenerateDocumentTest("186", "", "APPOINTMENT OF ASSESSOR", true);
    }

    @Test
    public void generateDocument_Part_Scot_189() throws Exception {
        testUtil.executeGenerateDocumentTest("189", "", "Notice of Stage 1 Equal Value Hearing", true);
    }

    @Test
    public void generateDocument_Part_Scot_190() throws Exception {
        testUtil.executeGenerateDocumentTest("190", "",
                "A member of the panel of independent experts designated by the Advisory Conciliation and", true);
    }

    @Test
    public void generateDocument_Part_Scot_191() throws Exception {
        testUtil.executeGenerateDocumentTest("191", "", "Requirement for Expert to Prepare a Report", true);
    }

    @Test
    public void generateDocument_Part_Scot_192() throws Exception {
        testUtil.executeGenerateDocumentTest("192", "", "The final hearing of this claim "
                + "shall take place at", true);
    }

    @Test
    public void generateDocument_Part_Scot_193() throws Exception {
        testUtil.executeGenerateDocumentTest("193", "", "REPORT OF INDEPENDENT EXPERT", true);
    }

    @Test
    public void generateDocument_Part_Scot_194() throws Exception {
        testUtil.executeGenerateDocumentTest("194", "", "Notice of Stage 2 Equal Value Hearing", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
