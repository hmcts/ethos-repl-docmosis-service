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
public class DocMosisPart10ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part10_1() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "1", "Written reasons will not be provided unless a written request is presented by either party within 14 days of the sending of this written record of the decision");
    }

    @Test
    public void generateDocument_Part10_2() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "2", "Written reasons will not be provided unless a written request is presented by either party within 14 days of the sending of this written record of the decision");
    }

    @Test
    public void generateDocument_Part10_3() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "3", "RESERVED REASONS");
    }

    @Test
    public void generateDocument_Part10_4() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "4", "The judgment in this case was reserved and will be sent to the parties in writing");
    }

    @Test
    public void generateDocument_Part10_5() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "5", "RESERVED JUDGMENT");
    }

    @Test
    public void generateDocument_Part10_6() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "6", "EMPLOYMENT TRIBUNAL JUDGMENT");
    }

    @Test
    public void generateDocument_Part10_7() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "7", "JUDGMENT");
    }

    @Test
    public void generateDocument_Part10_8() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "8", "JUDGMENT having been sent to the parties on");
    }

    @Test
    public void generateDocument_Part10_9() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "9", "Although it is out of time the Judge considers that it is in the interests of justice to extend the time limit for the application, which has been granted");
    }

    @Test
    public void generateDocument_Part10_10() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "10", "Although it is out of time the Judge considers that it is in the interests of justice to extend the time limit for the application, which is granted");
    }

    @Test
    public void generateDocument_Part10_11() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "11", "REFUSAL OF LATE REQUEST FOR WRITTEN REASONS");
    }

    @Test
    public void generateDocument_Part10_12() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "12", "REFUSAL OF LATE REQUEST FOR WRITTEN REASONS");
    }

    @Test
    public void generateDocument_Part10_13() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "13", "BY CONSENT");
    }

    @Test
    public void generateDocument_Part10_14() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "14", "CONSENT ORDER");
    }

    @Test
    public void generateDocument_Part10_15() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "15", "CERTIFICATE OF CORRECTION");
    }

    @Test
    public void generateDocument_Part10_16() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "", "CORRECTED JUDGMENT");
    }

    @After
    public void tearDown() {

    }
}
