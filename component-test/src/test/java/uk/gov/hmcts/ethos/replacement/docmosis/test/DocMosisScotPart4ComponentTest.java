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
        testUtil.executeGenerateDocumentTest("18", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_19() throws Exception {
        testUtil.executeGenerateDocumentTest("19", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_20() throws Exception {
        testUtil.executeGenerateDocumentTest("20", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_21() throws Exception {
        testUtil.executeGenerateDocumentTest("21", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_22() throws Exception {
        testUtil.executeGenerateDocumentTest("22", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_23() throws Exception {
        testUtil.executeGenerateDocumentTest("23", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_24() throws Exception {
        testUtil.executeGenerateDocumentTest("24", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_24_1() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", true);
    }

    @After
    public void tearDown() {

    }
}
