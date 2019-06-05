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
public class DocMosisPart11ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part11_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "1C", "RECONSIDERATION OF JUDGMENT: REFUSAL");
    }

    @Test
    public void generateDocument_Part11_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "1R", "RECONSIDERATION OF JUDGMENT: REFUSAL");
    }

    @Test
    public void generateDocument_Part11_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "2C", "EXTENSION OF TIME GRANTED FOR RECONSIDERATION OF JUDGMENT");
    }

    @Test
    public void generateDocument_Part11_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "2R", "EXTENSION OF TIME GRANTED FOR RECONSIDERATION OF JUDGMENT");
    }

    @Test
    public void generateDocument_Part11_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "3C", "RECONSIDERATION OF JUDGMENT: REJECTED");
    }

    @Test
    public void generateDocument_Part11_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "3R", "RECONSIDERATION OF JUDGMENT: REJECTED");
    }

    @Test
    public void generateDocument_Part11_4() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "4", "The grounds for the proposed reconsideration are that");
    }

    @Test
    public void generateDocument_Part11_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "5C", "Both parties are asked to write to us by \\[insert date\\] setting out their views on whether the application can be determined without a hearing");
    }

    @Test
    public void generateDocument_Part11_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "5R", "Both parties are asked to write to us by \\[insert date\\] setting out their views on whether the application can be determined without a hearing");
    }

    @Test
    public void generateDocument_Part11_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "6C", "The claimant’s application dated \\[insert date\\] for reconsideration of the judgment sent to the parties on \\[insert date\\] is refused");
    }

    @Test
    public void generateDocument_Part11_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "6R", "The respondent’s application dated \\[insert date\\] for reconsideration of the judgment sent to the parties on \\[insert date\\] is refused");
    }

    @Test
    public void generateDocument_Part11_7() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "7", "the interests of justice do not require a hearing, and the judgment dated");
    }

    @Test
    public void generateDocument_Part11_8() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "8", "If it is revoked, the case will be adjourned to be re-heard on its merits on a date");
    }

    @Test
    public void generateDocument_Part11_9() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "9", "If it is revoked, the re-hearing of the case will follow immediately");
    }

    @Test
    public void generateDocument_Part11_10() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "10", "UPON APPLICATION made by letter dated \\[insert date\\] to reconsider the judgment dated \\[insert date\\] under rule 71 of the Employment Tribunals Rules of Procedure 2013");
    }

    @Test
    public void generateDocument_Part11_11() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "11", "UPON APPLICATION made by letter dated [insert date] to reconsider the judgment under ");
    }

    @Test
    public void generateDocument_Part11_12() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "12", "UPON APPLICATION made by letter dated \\[insert date\\] to reconsider the judgment dated \\[insert date\\] under rule 71 of the Employment Tribunals Rules of Procedure 2013, and without a hearing");
    }

    @Test
    public void generateDocument_Part11_13() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "", "UPON APPLICATION made by letter dated [insert date] to reconsider the judgment under ");
    }

    @After
    public void tearDown() {

    }
}
