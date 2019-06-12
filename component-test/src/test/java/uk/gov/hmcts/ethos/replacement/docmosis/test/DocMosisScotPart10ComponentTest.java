package uk.gov.hmcts.ethos.replacement.docmosis.test;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.TestUtil;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
public class DocMosisScotPart10ComponentTest {


    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void generateDocument_Part_Scot_71() throws Exception {
        testUtil.executeGenerateDocumentTest("71", "", "LISTING OF CASE FOR PRELIMINARY HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_72() throws Exception {
        testUtil.executeGenerateDocumentTest("72", "", "NOTICE OF FINAL HEARING ON", true);
    }

    @Test
    public void generateDocument_Part_Scot_73() throws Exception {
        testUtil.executeGenerateDocumentTest("73", "", "Following initial consideration of the claim and response / On its own initiative", true);
    }

    @Test
    public void generateDocument_Part_Scot_74() throws Exception {
        testUtil.executeGenerateDocumentTest("74", "", "Insert this paragraph if no response or response struck out or dismissed", true);
    }

    @Test
    public void generateDocument_Part_Scot_75() throws Exception {
        testUtil.executeGenerateDocumentTest("75", "", "ACKNOWLEDGMENT OF A CLAIM AND", true);
    }

    @Test
    public void generateDocument_Part_Scot_76() throws Exception {
        testUtil.executeGenerateDocumentTest("76", "", "If a response is accepted, a preliminary hearing will be held", true);
    }

    @Test
    public void generateDocument_Part_Scot_78() throws Exception {
        testUtil.executeGenerateDocumentTest("78", "", "It is proposed to list this case for a final hearing. If you are aware of any reason why such a", true);
    }

    @Test
    public void generateDocument_Part_Scot_79() throws Exception {
        testUtil.executeGenerateDocumentTest("79", "", "Employment Judge requires you to complete the attached form in full and return it to this office by .  You do not have to send a copy of this form to the other party", true);
    }

    @Test
    public void generateDocument_Part_Scot_80() throws Exception {
        testUtil.executeGenerateDocumentTest("80", "", "DECISION ON REQUEST FOR FULL TRIBUNAL", true);
    }

    @Test
    public void generateDocument_Part_Scot_81() throws Exception {
        testUtil.executeGenerateDocumentTest("81", "", "RECONSIDERATION of Decision to reject a", true);
    }

    @Test
    public void generateDocument_Part_Scot_82() throws Exception {
        testUtil.executeGenerateDocumentTest("82", "", "Rule 27 (3) Employment Tribunals Rules of Procedure 2013", true);
    }

    @Test
    public void generateDocument_Part_Scot_83() throws Exception {
        testUtil.executeGenerateDocumentTest("83", "", "Rule 28 (3) Employment Tribunals Rules of Procedure 2013", true);
    }

    @Test
    public void generateDocument_Part_Scot_84() throws Exception {
        testUtil.executeGenerateDocumentTest("84", "", "NOTICE OF HEARING - RECONSIDERATION OF JUDGMENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_85() throws Exception {
        testUtil.executeGenerateDocumentTest("85", "", "NOTICE OF REMEDY HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_86() throws Exception {
        testUtil.executeGenerateDocumentTest("86", "", "NOTICE OF EXPENSES / PREPARATION TIME/WASTED COSTS HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_87() throws Exception {
        testUtil.executeGenerateDocumentTest("87", "", "NOTICE OF HEARING - RECONSIDERATION OF RULE 21 JUDGMENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_90() throws Exception {
        testUtil.executeGenerateDocumentTest("90", "", "NOTICE OF CONTINUED HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_91() throws Exception {
        testUtil.executeGenerateDocumentTest("91", "", "POSTPONEMENT OF HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_91_A() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "application for an extension of time to present a response should be considered at a hearing", true);
    }

    @Test
    public void generateDocument_Part_Scot_91_B() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "time to present a response to the employer’s contract claim", true);
    }

    @After
    public void tearDown() {

    }
}
