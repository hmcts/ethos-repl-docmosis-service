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
public class DocMosisScotPart6ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_34() throws Exception {
        testUtil.executeGenerateDocumentTest("34", "",
                "In accordance with the power set out in rule 29 of the Employment Tribunals Rules of", true);
    }

    @Test
    public void generateDocument_Part_Scot_34_A() throws Exception {
        testUtil.executeGenerateDocumentTest("34", "A",
                "Under Rule 29 of the Employment Tribunals Rules of Procedure 2013, the "
                        + "Employment Judge issues the", true);
    }

    @Test
    public void generateDocument_Part_Scot_35() throws Exception {
        testUtil.executeGenerateDocumentTest("35", "", "you shall provide to", true);
    }

    @Test
    public void generateDocument_Part_Scot_35_A() throws Exception {
        testUtil.executeGenerateDocumentTest("35", "A", "you shall provide to", true);
    }

    @Test
    public void generateDocument_Part_Scot_36() throws Exception {
        testUtil.executeGenerateDocumentTest("36", "", "you shall allow", true);
    }

    @Test
    public void generateDocument_Part_Scot_36_A() throws Exception {
        testUtil.executeGenerateDocumentTest("36", "A", "ORDER TO ALLOW INSPECTION OF", true);
    }

    @Test
    public void generateDocument_Part_Scot_37() throws Exception {
        testUtil.executeGenerateDocumentTest("37", "", "you shall provide", true);
    }

    @Test
    public void generateDocument_Part_Scot_37_A() throws Exception {
        testUtil.executeGenerateDocumentTest("37", "A", "you shall provide", true);
    }

    @Test
    public void generateDocument_Part_Scot_38() throws Exception {
        testUtil.executeGenerateDocumentTest("38", "",
                "Having considered any representations made by the parties an Employment Judge ORDERS that", true);
    }

    @Test
    public void generateDocument_Part_Scot_38_A() throws Exception {
        testUtil.executeGenerateDocumentTest("38", "A", "CONSIDERING CLAIMS TOGETHER", true);
    }

    @Test
    public void generateDocument_Part_Scot_39() throws Exception {
        testUtil.executeGenerateDocumentTest("39", "", "ORDER TO ATTEND AS A WITNESS", true);
    }

    @Test
    public void generateDocument_Part_Scot_40() throws Exception {
        testUtil.executeGenerateDocumentTest("40", "", "Your request for a witness order has been received", true);
    }

    @Test
    public void generateDocument_Part_Scot_41() throws Exception {
        testUtil.executeGenerateDocumentTest("41", "",
                "Your application for a witness order has been granted by an Employment Judge", true);
    }

    @Test
    public void generateDocument_Part_Scot_42() throws Exception {
        testUtil.executeGenerateDocumentTest("42", "", "A Witness Order has been sent to", true);
    }

    @Test
    public void generateDocument_Part_Scot_43() throws Exception {
        testUtil.executeGenerateDocumentTest("43", "", "SIST OF PROCEEDINGS", true);
    }

    @Test
    public void generateDocument_Part_Scot_44() throws Exception {
        testUtil.executeGenerateDocumentTest("44", "", "ORDER ADDING A PERSON AS A PARTY", true);
    }

    @Test
    public void generateDocument_Part_Scot_45() throws Exception {
        testUtil.executeGenerateDocumentTest("45", "", "ORDER REMOVING A PERSON AS A PARTY", true);
    }

    @Test
    public void generateDocument_Part_Scot_46() throws Exception {
        testUtil.executeGenerateDocumentTest("46", "", "ORDER SUBSTITUTING A PERSON AS A", true);
    }

    @Test
    public void generateDocument_Part_Scot_47() throws Exception {
        testUtil.executeGenerateDocumentTest("47", "", "CASE MANAGEMENT ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_48() throws Exception {
        testUtil.executeGenerateDocumentTest("48", "", "CASE MANAGEMENT APPLICATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_49() throws Exception {
        testUtil.executeGenerateDocumentTest("49", "", "REFUSAL OF CASE MANAGEMENT APPLICATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_50() throws Exception {
        testUtil.executeGenerateDocumentTest("50", "", "CASE MANAGEMENT APPLICATION GRANTED", true);
    }

    @Test
    public void generateDocument_Part_Scot_50_A() throws Exception {
        testUtil.executeGenerateDocumentTest("50", "A", "ORDER GIVING LEAVE TO AMEND A", true);
    }

    @Test
    public void generateDocument_Part_Scot_51() throws Exception {
        testUtil.executeGenerateDocumentTest("51", "", "NOTIFICATION OF A LEAD CASE", true);
    }

    @Test
    public void generateDocument_Part_Scot_52() throws Exception {
        testUtil.executeGenerateDocumentTest("52", "", "Cases : as per attached Schedule", true);
    }

    @Test
    public void generateDocument_Part_Scot_53() throws Exception {
        testUtil.executeGenerateDocumentTest("53", "", "NOTIFICATION OF NEW LEAD CASE", true);
    }

    @Test
    public void generateDocument_Part_Scot_54() throws Exception {
        testUtil.executeGenerateDocumentTest("54", "", "RESTRICTED REPORTING ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_55() throws Exception {
        testUtil.executeGenerateDocumentTest("55", "", "Restricted Reporting Order", true);
    }

    @Test
    public void generateDocument_Part_Scot_56() throws Exception {
        testUtil.executeGenerateDocumentTest("56", "", "ORDER TO PREVENT DISCLOSURE OF", true);
    }

    @Test
    public void generateDocument_Part_Scot_57() throws Exception {
        testUtil.executeGenerateDocumentTest("57", "", "CLAIM/RESPONSE DISMISSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_57_A() throws Exception {
        testUtil.executeGenerateDocumentTest("57", "A", "APPLICATION TO SET ASIDE DISMISSAL OF", true);
    }

    @Test
    public void generateDocument_Part_Scot_57_B() throws Exception {
        testUtil.executeGenerateDocumentTest("57", "B", "DISMISSAL OF", true);
    }

    @Test
    public void generateDocument_Part_Scot_57_C() throws Exception {
        testUtil.executeGenerateDocumentTest("57", "C", "Notice of the "
                + "hearing at which your application will be", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
