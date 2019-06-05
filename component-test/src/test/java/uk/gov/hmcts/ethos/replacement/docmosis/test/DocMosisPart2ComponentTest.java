package uk.gov.hmcts.ethos.replacement.docmosis.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.TestUtil;

@Category(ComponentTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class DocMosisPart2ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part1_1() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "1", "NO RESPONSE REQUIRED");
    }

    @Test
    public void generateDocument_Part1_2() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "2", "You are however required to enter a response to the other claims");
    }

    @Test
    public void generateDocument_Part1_3() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "3", "Enclosed is a copy of the reply received from the claimant");
    }

    @Test
    public void generateDocument_Part1_4() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "4", "NOTICE OF APPLICATION FOR INTERIM RELIEF");
    }

    @Test
    public void generateDocument_Part1_5() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "5", "NOTICE OF HEARING - INTERIM RELIEF");
    }

    @Test
    public void generateDocument_Part1_6() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "6", "To submit a response.*If a respondent wishes to defend the claim");
    }

    @Test
    public void generateDocument_Part1_6A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "6A", "MULTIPLE CLAIMS");
    }

    @Test
    public void generateDocument_Part1_7() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "7", "The law protects workers from having unauthorised deductions made from their wages");
    }

    @Test
    public void generateDocument_Part1_8() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "8", "These Orders are made under rules 29 and 30 of the Employment Tribunals Rules");
    }

    @Test
    public void generateDocument_Part1_9() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "9", "FURTHER COMPLAINTS ACCEPTED AFTER RECONSIDERATION");
    }

    @Test
    public void generateDocument_Part1_10() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "10", "RE-SENDING OF CLAIM");
    }

    @Test
    public void generateDocument_Part1_10A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "10A", "SUBSTITUTED SERVICE OF CLAIM");
    }

    @Test
    public void generateDocument_Part1_11() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "11", "RESPONSE ACCEPTED");
    }

    @Test
    public void generateDocument_Part1_11A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "11A", "RETURN OF DOCUMENTS");
    }

    @Test
    public void generateDocument_Part1_12() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "12", "because it has not been presented on a prescribed form as required by rule 16 of the above Rules");
    }

    @Test
    public void generateDocument_Part1_13() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "13", "the respondent’s full name");
    }

    @Test
    public void generateDocument_Part1_14() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "14", "under rule 18 of the above Rules the response must be rejected");
    }

    @Test
    public void generateDocument_Part1_15() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "15", "RESPONSE REJECTION - YOUR QUESTIONS ANSWERED");
    }

    @Test
    public void generateDocument_Part1_16() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "16", "reconsideration of the decision to reject your response cannot be accepted");
    }

    @Test
    public void generateDocument_Part1_17() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "17", "has reconsidered the decision and has decided that your response can be accepted");
    }

    @Test
    public void generateDocument_Part1_18() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "18", "RESPONSE REJECTION – RECONSIDERATION HEARING");
    }

    @Test
    public void generateDocument_Part1_18A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "18A", "RESPONSE REJECTION - RECONSIDERATION - DISMISSED");
    }

    @Test
    public void generateDocument_Part1_19() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "19", "APPLICATION FOR EXTENSION OF TIME TO SUBMIT RESPONSE");
    }

    @Test
    public void generateDocument_Part1_20() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "20", "EXTENSION OF TIME FOR RESPONSE GRANTED ");
    }

    @Test
    public void generateDocument_Part1_21() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "21", "EXTENSION OF TIME FOR RESPONSE REFUSED");
    }

    @Test
    public void generateDocument_Part1_22() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "22", "has refused the application for an extension of time");
    }

    @Test
    public void generateDocument_Part1_23() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "23", "It is not clear whether you have copied your application to the claimant as required by rule 20");
    }

    @Test
    public void generateDocument_Part1_24() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "24", "has granted your application and has extended the time limit to enable the response to be accepted");
    }

    @Test
    public void generateDocument_Part1_25() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "25", "APPLICATION FOR EXTENSION OF TIME TO PRESENT RESPONSE");
    }

    @Test
    public void generateDocument_Part1_26() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "26", "NO RESPONSE RECEIVED");
    }

    @Test
    public void generateDocument_Part1_26A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "26A", "CASE NOT CONTESTED");
    }

    @Test
    public void generateDocument_Part1_27() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "", "Response – Amendment Granted");
    }

    @After
    public void tearDown() {

    }
}
