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
public class DocMosisScotPart11ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_92() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "", "EMPLOYMENT TRIBUNAL JUDGMENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_92_A() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "A", "Insert judgment details", true);
    }

    @Test
    public void generateDocument_Part_Scot_92_B() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "B",
                "Recoupment of Jobseeker’s Allowance, Income Support and Income related Employment and", true);
    }

    @Test
    public void generateDocument_Part_Scot_92_C() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "C",
                "Recoupment of Jobseeker's Allowance, Income Support and income-related", true);
    }

    @Test
    public void generateDocument_Part_Scot_92_D() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "D",
                "Amount by which the monetary award exceeds the prescribed element", true);
    }

    @Test
    public void generateDocument_Part_Scot_92_E() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "E", "NOTIFICATION OF RECOUPMENT - PROTECTIVE AWARD", true);
    }

    @Test
    public void generateDocument_Part_Scot_93() throws Exception {
        testUtil.executeGenerateDocumentTest("93", "", "REASONS FOR DECISION OF EMPLOYMENT TRIBUNAL", true);
    }

    @Test
    public void generateDocument_Part_Scot_93A() throws Exception {
        testUtil.executeGenerateDocumentTest("93", "A", "REFUSAL OF LATE REQUEST FOR WRITTEN REASONS", true);
    }

    @Test
    public void generateDocument_Part_Scot_93B() throws Exception {
        testUtil.executeGenerateDocumentTest("93", "B",
                "Although your application is out of time the Judge considers that it is in the "
                        + "interests of justice to", true);
    }

    @Test
    public void generateDocument_Part_Scot_94() throws Exception {
        testUtil.executeGenerateDocumentTest("94", "", "NOTE FOLLOWING A PRELIMINARY HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_95() throws Exception {
        testUtil.executeGenerateDocumentTest("95", "",
                "In accordance with the power set out in Rule 69 of the Employment Tribunal Rules of Procedure", true);
    }

    @Test
    public void generateDocument_Part_Scot_96() throws Exception {
        testUtil.executeGenerateDocumentTest("96", "",
                "I enclose a corrected copy of the Judgment / Case Management Order and a "
                        + "certificate of correction", true);
    }

    @Test
    public void generateDocument_Part_Scot_97() throws Exception {
        testUtil.executeGenerateDocumentTest("97", "", "INTEREST ON TRIBUNAL AWARDS: GUIDANCE NOTE", true);
    }

    @Test
    public void generateDocument_Part_Scot_98() throws Exception {
        testUtil.executeGenerateDocumentTest("98", "", "GUIDANCE NOTE IN DISCRIMINATION CASES", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
