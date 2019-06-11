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
public class DocMosisPart6ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part6_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "1C", "STRIKE OUT WARNING");
    }

    @Test
    public void generateDocument_Part6_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "1R", "STRIKE OUT WARNING");
    }

    @Test
    public void generateDocument_Part6_2() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "2", "The claim is struck out");
    }

    @Test
    public void generateDocument_Part6_3() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "3", "the Tribunal gave the claimant an opportunity to make representations or to request a hearing, as to why the the complaint of");
    }

    @Test
    public void generateDocument_Part6_4() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "4", "The response is struck out\\.");
    }

    @Test
    public void generateDocument_Part6_5() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "5", "The response is struck out in part");
    }

    @Test
    public void generateDocument_Part6_6() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "6", "Thank you for informing the Tribunal that you have withdrawn your claim");
    }

    @Test
    public void generateDocument_Part6_7() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "7", "WITHDRAWAL OF PART OF CLAIM");
    }

    @Test
    public void generateDocument_Part6_8() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "8", "The proceedings are dismissed following a withdrawal of the claim by the claimant");
    }

    @Test
    public void generateDocument_Part6_9() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "9", "dismissed following a withdrawal by the claimant");
    }

    @Test
    public void generateDocument_Part6_10() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "10", "has directed that a judgment dismissing the claim is not issued");
    }

    @Test
    public void generateDocument_Part6_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "11C", "the hearing will be cancelled");
    }

    @Test
    public void generateDocument_Part6_12() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "", "the hearing will be cancelled");
    }

    @After
    public void tearDown() {

    }
}
