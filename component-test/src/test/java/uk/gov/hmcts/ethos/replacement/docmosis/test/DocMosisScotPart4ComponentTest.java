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
public class DocMosisScotPart4ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_18() throws Exception {
        testUtil.executeGenerateDocumentTest("18", "", "ACKNOWLEDGMENT OF EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_19() throws Exception {
        testUtil.executeGenerateDocumentTest("19", "", "Rejection of EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_19_A() throws Exception {
        testUtil.executeGenerateDocumentTest("19", "A", "RECONSIDERATION OF DECISION TO REJECT EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_20() throws Exception {
        testUtil.executeGenerateDocumentTest("20", "", "NOTICE OF EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_21() throws Exception {
        testUtil.executeGenerateDocumentTest("21", "", "ACKNOWLEDGMENT OF RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_22() throws Exception {
        testUtil.executeGenerateDocumentTest("22", "", "NOTICE OF RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_23() throws Exception {
        testUtil.executeGenerateDocumentTest("23", "", "REJECTION OF RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_24() throws Exception {
        testUtil.executeGenerateDocumentTest("24", "", "EXTENSION OF TIME FOR RESPONSE TO EMPLOYER’S CONTRACT CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_25() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "EXTENSION OF TIME FOR RESPONSE TO CONTRACT CLAIM REFUSED", true);
    }

    @After
    public void tearDown() {

    }
}
