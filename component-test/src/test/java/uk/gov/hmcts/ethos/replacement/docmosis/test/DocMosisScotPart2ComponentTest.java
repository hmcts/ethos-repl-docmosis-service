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
public class DocMosisScotPart2ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_8() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "", "EXTENSION OF TIME FOR RESPONSE GRANTED", true);
    }

    @Test
    public void generateDocument_Part_Scot_9() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "", "EXTENSION OF TIME FOR RESPONSE REFUSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_10() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "", "I have decided in accordance with Rule 17 that your response cannot be accepted because of the following defect", true);
    }

    @Test
    public void generateDocument_Part_Scot_11() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "", "We have received your response to the claim shown above in this office", true);
    }

    @Test
    public void generateDocument_Part_Scot_12() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "", "RECONSIDERATION OF DECISION TO REJECT RESPONSE", true);
    }

    @Test
    public void generateDocument_Part_Scot_13() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "", "ACKNOWLEDGEMENT OF RESPONSE", true);
    }

    @Test
    public void generateDocument_Part_Scot_14() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "", "The enclosed response has been accepted", true);
    }

    @Test
    public void generateDocument_Part_Scot_14_1() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "ACCEPTANCE OF ADDITIONAL PART OF CLAIM", true);
    }

    @After
    public void tearDown() {

    }
}
