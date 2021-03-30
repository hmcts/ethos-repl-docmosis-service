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
public class DocMosisPart5ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part5_1A() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1A", "will not be considered until you have done so");
    }

    @Test
    public void generateDocument_Part5_1B() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1B", "the correspondence has been considered anyway");
    }

    @Test
    public void generateDocument_Part5_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1C", "you have sent a copy to the other party");
    }

    @Test
    public void generateDocument_Part5_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1R", "you have sent a copy to the other party");
    }

    @Test
    public void generateDocument_Part5_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "2C", "the application is accepted and the need for the respondent");
    }

    @Test
    public void generateDocument_Part5_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "2R", "the application is accepted and the need for the claimant");
    }

    @Test
    public void generateDocument_Part5_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "3C", "The case remains listed for hearing on");
    }

    @Test
    public void generateDocument_Part5_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "3R", "The case remains listed for hearing on");
    }

    @Test
    public void generateDocument_Part5_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "4C", "I refer to your application dated");
    }

    @Test
    public void generateDocument_Part5_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "4R", "I refer to your application dated");
    }

    @Test
    public void generateDocument_Part5_5() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "5",
                "is considering listing this case for a preliminary hearing to discuss case");
    }

    @Test
    public void generateDocument_Part5_5A() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "5A", "CASE MANAGEMENT ORDER");
    }

    @Test
    public void generateDocument_Part5_6() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "6",
                "Having considered the proposals of the parties, Employment Judge");
    }

    @Test
    public void generateDocument_Part5_7() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "7",
                "Having considered the proposals of the parties, Employment Judge");
    }

    @Test
    public void generateDocument_Part5_8() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "8", "Case No\\.");
    }

    @Test
    public void generateDocument_Part5_9C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "9C", "CASE MANAGEMENT ORDER- LEAVE TO AMEND CLAIM");
    }

    @Test
    public void generateDocument_Part5_9R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "9R", "CASE MANAGEMENT ORDER- LEAVE TO AMEND CLAIM");
    }

    @Test
    public void generateDocument_Part5_10() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "10", "POSTPONEMENT OF HEARING");
    }

    @Test
    public void generateDocument_Part5_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "11C", "POSTPONEMENT ORDER");
    }

    @Test
    public void generateDocument_Part5_11R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "11R", "POSTPONEMENT ORDER");
    }

    @Test
    public void generateDocument_Part5_12C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "12C", "POSTPONEMENT REQUEST REFUSED");
    }

    @Test
    public void generateDocument_Part5_12R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "12R", "POSTPONEMENT REQUEST REFUSED");
    }

    @Test
    public void generateDocument_Part5_13C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "13C", "ORDER TO DISCLOSE INFORMATION");
    }

    @Test
    public void generateDocument_Part5_13R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "13R", "ORDER TO DISCLOSE INFORMATION");
    }

    @Test
    public void generateDocument_Part5_14C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "14C", "ORDER TO DISCLOSE DOCUMENTS");
    }

    @Test
    public void generateDocument_Part5_14R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "14R", "ORDER TO DISCLOSE DOCUMENTS");
    }

    @Test
    public void generateDocument_Part5_15C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "15C", "APPLICATION FOR WITNESS ORDER");
    }

    @Test
    public void generateDocument_Part5_15R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "15R", "APPLICATION FOR WITNESS ORDER");
    }

    @Test
    public void generateDocument_Part5_16() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "16", "REFUSAL OF WITNESS ORDER");
    }

    @Test
    public void generateDocument_Part5_17() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "17", "WITNESS ORDER TO ATTEND TO PRODUCE DOCUMENTS");
    }

    @Test
    public void generateDocument_Part5_18() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "18", "WITNESS ORDER TO GIVE EVIDENCE");
    }

    @Test
    public void generateDocument_Part5_19() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "19",
                "Your application for a witness order has been granted by Employment Judge");
    }

    @Test
    public void generateDocument_Part5_19A() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "19A", "A Witness Order has been sent to");
    }

    @Test
    public void generateDocument_Part5_20() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "20", "has granted your request to set aside the Witness");
    }

    @Test
    public void generateDocument_Part5_21() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "21", "has refused your request to set aside the Witness Order");
    }

    @Test
    public void generateDocument_Part5_22() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "22", "ORDER ADDING A PARTY");
    }

    @Test
    public void generateDocument_Part5_23() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "23", "ORDER REMOVING PARTY");
    }

    @Test
    public void generateDocument_Part5_24() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "24", "CONSIDERING CLAIMS TOGETHER");
    }

    @Test
    public void generateDocument_Part5_25() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "25", "orders that the following claims be heard together");
    }

    @Test
    public void generateDocument_Part5_26() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "26", "will stand dismissed without further order");
    }

    @Test
    public void generateDocument_Part5_27() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "27",
                "The respondent will then be entitled to notice of any hearings and decisions");
    }

    @Test
    public void generateDocument_Part5_28() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "28", "CONFIRMATION OF DISMISSAL OF CLAIM");
    }

    @Test
    public void generateDocument_Part5_29() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "29", "CONFIRMATION OF DISMISSAL OF RESPONSE");
    }

    @Test
    public void generateDocument_Part5_30() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "30", "SETTING ASIDE DISMISSAL OF CLAIM");
    }

    @Test
    public void generateDocument_Part5_31() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "31", "SETTING ASIDE DISMISSAL OF RESPONSE");
    }

    @Test
    public void generateDocument_Part5_32() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "32", "APPLICATION TO SET ASIDE DISMISSAL OF CLAIM");
    }

    @Test
    public void generateDocument_Part5_33() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "33", "APPLICATION TO SET ASIDE DISMISSAL OF RESPONSE");
    }

    @Test
    public void generateDocument_Part5_34() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "34", "APPLICATION TO SET ASIDE DISMISSAL OF CLAIM");
    }

    @Test
    public void generateDocument_Part5_35() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "35",
                "application to set aside the dismissal of your response following non-compliance with the Unless");
    }

    @Test
    public void generateDocument_Part5_36() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "36",
                "After considering the objections to the proposal to strike out");
    }

    @Test
    public void generateDocument_Part5_37() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "37",
                "After considering the objections to the proposal to strike out");
    }

    @Test
    public void generateDocument_Part5_38() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "38",
                "We enclose a copy of a letter which we have received from the respondent alleging that you");
    }

    @Test
    public void generateDocument_Part5_39() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "39",
                "We enclose a copy of a letter which we have received from the claimant alleging that you have");
    }

    @Test
    public void generateDocument_Part5_40() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "40", "REINSTATEMENT ORDER");
    }

    @Test
    public void generateDocument_Part5_41() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "41", "REFUSAL OF REINSTATEMENT");
    }

    @Test
    public void generateDocument_Part5_42() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "42", "ORDER STAYING PROCEEDINGS");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
