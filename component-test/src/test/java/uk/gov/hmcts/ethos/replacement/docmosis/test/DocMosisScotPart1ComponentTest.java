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
public class DocMosisScotPart1ComponentTest {


    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void generateDocument_Part_Scot_1() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", "I have received your claim form but am unable to accept it because it is defective for the following", true);
    }

    @Test
    public void generateDocument_Part_Scot_2() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "", "REJECTION OF CLAIM/PART OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "", "Your claim has been accepted at this office and the case file has been transferred to the Aberdeen office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_1() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "1", "Your claim has been accepted at this office and the case file has been transferred to the Aberdeen office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_2() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "2", "Your claim has been accepted at this office and the case file has been transferred to the Dundee office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "3", "Your claim has been accepted at this office and the case file has been transferred to the Edinburgh office", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_4() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "4", "Your claim has been accepted at this office. You should quote the case number shown above on", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_5() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "5", "I am writing to you because your claim/complaint of", true);
    }

    @Test
    public void generateDocument_Part_Scot_4() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "", "RECONSIDERATION OF DECISION TO REJECT CLAIM/PART OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_5() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "", "Employment Judge [insert name] has reconsidered the decision without a hearing and has", true);
    }

    @Test
    public void generateDocument_Part_Scot_6() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "", "RECONSIDERATION - REJECTION CONFIRMED", true);
    }

    @Test
    public void generateDocument_Part_Scot_7() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_1() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "1", "You should do this by completing and returning the enclosed response form to ETS, Mezzanine Floor, Atholl House, 84-88 Guild Street, Aberdeen AB11 6LT", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_2() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "2", "You should do this by completing and returning the enclosed response form to ETS, Ground Floor Block C Caledonian House Greenmarket Dundee DD1 4QX", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_3() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "3", "You should do this by completing and returning the enclosed response form to ETS, 54 – 56 Melville Street. Edinburgh EH3 7HF", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_4() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "4", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_5() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "PART OF CLAIM \\(delete as appropriate\\) ACCEPTED OUT OF TIME", true);
    }

    @After
    public void tearDown() {

    }
}
