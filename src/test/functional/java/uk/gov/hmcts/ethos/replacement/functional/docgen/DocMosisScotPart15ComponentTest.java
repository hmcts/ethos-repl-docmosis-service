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
public class DocMosisScotPart15ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_159() throws Exception {
        testUtil.executeGenerateDocumentTest("159", "", "Information pack for Judicial Mediation", true);
    }

    @Test
    public void generateDocument_Part_Scot_160() throws Exception {
        testUtil.executeGenerateDocumentTest("160", "",
                "CRITERIA FOR THE IDENTIFICATION OF CASES THAT MIGHT BE CONSIDERED", true);
    }

    @Test
    public void generateDocument_Part_Scot_161() throws Exception {
        testUtil.executeGenerateDocumentTest("161", "",
                "First part for completion by Employment Judge who conducted the PH", true);
    }

    @Test
    public void generateDocument_Part_Scot_162() throws Exception {
        testUtil.executeGenerateDocumentTest("162", "", "Judicial Mediation – Note to Parties", true);
    }

    @Test
    public void generateDocument_Part_Scot_163() throws Exception {
        testUtil.executeGenerateDocumentTest("163", "",
                "At the preliminary hearing which you recently attended parties indicated "
                        + "that they wished some time to", true);
    }

    @Test
    public void generateDocument_Part_Scot_164() throws Exception {
        testUtil.executeGenerateDocumentTest("164", "", "Urgent response required re interest", true);
    }

    @Test
    public void generateDocument_Part_Scot_165() throws Exception {
        testUtil.executeGenerateDocumentTest("165", "", "Response awaited from other party", true);
    }

    @Test
    public void generateDocument_Part_Scot_166() throws Exception {
        testUtil.executeGenerateDocumentTest("166", "", "Judicial mediation not pursued", true);
    }

    @Test
    public void generateDocument_Part_Scot_167() throws Exception {
        testUtil.executeGenerateDocumentTest("167", "", "Judicial Mediation – Not pursued", true);
    }

    @Test
    public void generateDocument_Part_Scot_168() throws Exception {
        testUtil.executeGenerateDocumentTest("168", "",
                "The file has been referred to the Vice-President to consider whether judicial "
                        + "mediation could be offered", true);
    }

    @Test
    public void generateDocument_Part_Scot_169() throws Exception {
        testUtil.executeGenerateDocumentTest("169", "",
                "I understand that you expressed interest in an offer of judicial mediation", true);
    }

    @Test
    public void generateDocument_Part_Scot_170() throws Exception {
        testUtil.executeGenerateDocumentTest("170", "", "NOTICE OF PRELIMINARY HEARING BY TELEPHONE", true);
    }

    @Test
    public void generateDocument_Part_Scot_171() throws Exception {
        testUtil.executeGenerateDocumentTest("171", "",
                "The Vice-President has directed that there should be a Judicial Mediation which "
                        + "will take place in private", true);
    }

    @Test
    public void generateDocument_Part_Scot_172() throws Exception {
        testUtil.executeGenerateDocumentTest("172", "", "Judicial Mediation Report", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
