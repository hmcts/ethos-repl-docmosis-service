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
public class DocMosisScotPart14ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part_Scot_110() throws Exception {
        testUtil.executeGenerateDocumentTest("110", "", "ACKNOWLEDGEMENT OF CORRESPONDENCE", true);
    }

    @Test
    public void generateDocument_Part_Scot_110A() throws Exception {
        testUtil.executeGenerateDocumentTest("110", "A", "CORRESPONDENCE NOT COPIED TO OTHER PARTY", true);
    }

    @Test
    public void generateDocument_Part_Scot_111() throws Exception {
        testUtil.executeGenerateDocumentTest("111", "", "REQUEST FOR ADVICE", true);
    }

    @Test
    public void generateDocument_Part_Scot_112() throws Exception {
        testUtil.executeGenerateDocumentTest("112", "", "CORRESPONDENCE ENCLOSED – FOR INFORMATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_113() throws Exception {
        testUtil.executeGenerateDocumentTest("113", "", "ADDRESSING CORRESPONDENCE", true);
    }

    @Test
    public void generateDocument_Part_Scot_114() throws Exception {
        testUtil.executeGenerateDocumentTest("114", "", "AUTHORITY TO ACT IN EMPLOYMENT TRIBUNAL PROCEEDINGS", true);
    }

    @Test
    public void generateDocument_Part_Scot_115() throws Exception {
        testUtil.executeGenerateDocumentTest("115", "", "REQUEST TO TRANSFER CASE", true);
    }

    @Test
    public void generateDocument_Part_Scot_116() throws Exception {
        testUtil.executeGenerateDocumentTest("116", "", "TRANSFER OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_117() throws Exception {
        testUtil.executeGenerateDocumentTest("117", "", "REQUEST FOR INFORMATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_118() throws Exception {
        testUtil.executeGenerateDocumentTest("118", "", "COPYING CORRESPONDENCE TO THE TRIBUNAL", true);
    }

    @Test
    public void generateDocument_Part_Scot_119C() throws Exception {
        testUtil.executeGenerateDocumentTest("119", "C", "Respondent / Respondent’s representative", true);
    }

    @Test
    public void generateDocument_Part_Scot_119R() throws Exception {
        testUtil.executeGenerateDocumentTest("119", "R", "Claimant / Claimant’s representative", true);
    }

    @Test
    public void generateDocument_Part_Scot_120() throws Exception {
        testUtil.executeGenerateDocumentTest("120", "", "The above named claimant’s claim for", true);
    }

    @Test
    public void generateDocument_Part_Scot_121() throws Exception {
        testUtil.executeGenerateDocumentTest("121", "", "TRANSFER OF PROCEEDINGS FROM SCOTLAND", true);
    }

    @Test
    public void generateDocument_Part_Scot_122() throws Exception {
        testUtil.executeGenerateDocumentTest("122", "", "REQUEST FROM TRIBUNAL", true);
    }

    @Test
    public void generateDocument_Part_Scot_123C() throws Exception {
        testUtil.executeGenerateDocumentTest("123", "C", "This letter has been copied to the respondent", true);
    }

    @Test
    public void generateDocument_Part_Scot_123R() throws Exception {
        testUtil.executeGenerateDocumentTest("123", "R", "This letter has been copied to the respondent", true);
    }

    @Test
    public void generateDocument_Part_Scot_124C() throws Exception {
        testUtil.executeGenerateDocumentTest("124", "C",
                "Please find enclosed the documents that you lodged before the Tribunal", true);
    }

    @Test
    public void generateDocument_Part_Scot_124R() throws Exception {
        testUtil.executeGenerateDocumentTest("124", "R",
                "Please find enclosed the documents that you lodged before the Tribunal", true);
    }

    @Test
    public void generateDocument_Part_Scot_125C() throws Exception {
        testUtil.executeGenerateDocumentTest("125", "C", "CHANGE OF ADDRESS FOR CORRESPONDENCE", true);
    }

    @Test
    public void generateDocument_Part_Scot_125R() throws Exception {
        testUtil.executeGenerateDocumentTest("125", "R", "CHANGE OF ADDRESS FOR CORRESPONDENCE", true);
    }

    @Test
    public void generateDocument_Part_Scot_126() throws Exception {
        testUtil.executeGenerateDocumentTest("126", "", "request for a transfer of the proceedings to the", true);
    }

    @Test
    public void generateDocument_Part_Scot_127() throws Exception {
        testUtil.executeGenerateDocumentTest("127", "", "insert appropriate letter heading", true);
    }

    @Test
    public void generateDocument_Part_Scot_128() throws Exception {
        testUtil.executeGenerateDocumentTest("128", "", "SIST OF PROCEEDINGS", true);
    }

    @Test
    public void generateDocument_Part_Scot_129C() throws Exception {
        testUtil.executeGenerateDocumentTest("129", "C", "SOUL AND CONSCIENCE CERTIFICATE", true);
    }

    @Test
    public void generateDocument_Part_Scot_129R() throws Exception {
        testUtil.executeGenerateDocumentTest("129", "R", "SOUL AND CONSCIENCE CERTIFICATE", true);
    }

    @Test
    public void generateDocument_Part_Scot_130() throws Exception {
        testUtil.executeGenerateDocumentTest("130", "", "REQUEST NOT TO COPY CLAIM TO RESPONDENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_131() throws Exception {
        testUtil.executeGenerateDocumentTest("131", "", "CLAIM IN WHICH SECRETARY OF STATE MAY BE LIABLE", true);
    }

    @Test
    public void generateDocument_Part_Scot_132() throws Exception {
        testUtil.executeGenerateDocumentTest("132", "", "RE-SENDING OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_133() throws Exception {
        testUtil.executeGenerateDocumentTest("133", "", "The name you have provided "
                + "for the respondent to your claim", true);
    }

    @Test
    public void generateDocument_Part_Scot_134() throws Exception {
        testUtil.executeGenerateDocumentTest("134", "", "RECALL OF SIST TO PROCEEDINGS", true);
    }

    @Test
    public void generateDocument_Part_Scot_135() throws Exception {
        testUtil.executeGenerateDocumentTest("135", "", "RESPONDENT IN ADMINISTRATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_136() throws Exception {
        testUtil.executeGenerateDocumentTest("136", "", "It is not clear that you have obtained the consent of", true);
    }

    @Test
    public void generateDocument_Part_Scot_137() throws Exception {
        testUtil.executeGenerateDocumentTest("137", "",
                "The respondent company is in administration. Neither the consent of the administrator nor the", true);
    }

    @Test
    public void generateDocument_Part_Scot_138() throws Exception {
        testUtil.executeGenerateDocumentTest("138", "", "COMPULSORY LIQUIDATION OF RESPONDENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_139() throws Exception {
        testUtil.executeGenerateDocumentTest("139", "",
                "an Employment Judge proposes to order that the claim be struck out because the claim has", true);
    }

    @Test
    public void generateDocument_Part_Scot_140() throws Exception {
        testUtil.executeGenerateDocumentTest("140", "",
                "proceedings to be instituted or continued as required by the Insolvency Act 1986.  No such "
                        + "consent has been obtained", true);
    }

    @Test
    public void generateDocument_Part_Scot_141() throws Exception {
        testUtil.executeGenerateDocumentTest("141", "", "CONSENT TO EMPLOYMENT JUDGE SITTING ALONE", true);
    }

    @Test
    public void generateDocument_Part_Scot_142() throws Exception {
        testUtil.executeGenerateDocumentTest("142", "", "CONSENT TO A TWO PERSON TRIBUNAL", true);
    }

    @Test
    public void generateDocument_Part_Scot_143() throws Exception {
        testUtil.executeGenerateDocumentTest("143", "", "CHANGE OF TIME OF HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_144() throws Exception {
        testUtil.executeGenerateDocumentTest("144", "",
                "has ordered that the time allocated for the hearing shall be", true);
    }

    @Test
    public void generateDocument_Part_Scot_145() throws Exception {
        testUtil.executeGenerateDocumentTest("145", "",
                "I enclose an extract of the award as entered in the Register of the Employment Tribunals", true);
    }

    @Test
    public void generateDocument_Part_Scot_146() throws Exception {
        testUtil.executeGenerateDocumentTest("146", "", "Enforcement of Orders in Other "
                + "Jurisdictions", true);
    }

    @Test
    public void generateDocument_Part_Scot_147() throws Exception {
        testUtil.executeGenerateDocumentTest("147", "", "request for extract of award", true);
    }

    @Test
    public void generateDocument_Part_Scot_148() throws Exception {
        testUtil.executeGenerateDocumentTest("148", "", "RESPONDENT COMPANY DISSOLVED", true);
    }

    @Test
    public void generateDocument_Part_Scot_149() throws Exception {
        testUtil.executeGenerateDocumentTest("149", "",
                "You have not informed us that you have applied to have the Respondent company restored to the "
                        + "Register of Companies", true);
    }

    @Test
    public void generateDocument_Part_Scot_150() throws Exception {
        testUtil.executeGenerateDocumentTest("150", "", "The claimant has not applied to have the company", true);
    }

    @Test
    public void generateDocument_Part_Scot_151() throws Exception {
        testUtil.executeGenerateDocumentTest("151", "", "REDUNDANCY AND OTHER PAYMENTS", true);
    }

    @Test
    public void generateDocument_Part_Scot_152() throws Exception {
        testUtil.executeGenerateDocumentTest("152", "", "Covering Message", true);
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
