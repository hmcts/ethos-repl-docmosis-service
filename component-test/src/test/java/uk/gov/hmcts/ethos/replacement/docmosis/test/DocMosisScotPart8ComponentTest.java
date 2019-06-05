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
public class DocMosisScotPart8ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_64() throws Exception {
        testUtil.executeGenerateDocumentTest("64", "", "WITHDRAWAL OF CLAIM / PART OF CLAIM", true);
    }

    @Test
    public void generateDocument_Part_Scot_65() throws Exception {
        testUtil.executeGenerateDocumentTest("65", "", "CANCELLATION OF HEARING", true);
    }

    @Test
    public void generateDocument_Part_Scot_66() throws Exception {
        testUtil.executeGenerateDocumentTest("66", "", "CASE REMAINS LISTED", true);
    }

    @Test
    public void generateDocument_Part_Scot_67() throws Exception {
        testUtil.executeGenerateDocumentTest("67", "", "We have received notice from both parties that they have reached an agreement", true);
    }

    @Test
    public void generateDocument_Part_Scot_68() throws Exception {
        testUtil.executeGenerateDocumentTest("68", "", "Acas have sent us notice that terms of settlement have been agreed between the parties", true);
    }

    public void generateDocument_Part_Scot_69() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "The claim/ complaint of", true);
    }

    @After
    public void tearDown() {

    }
}
