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
public class DocMosisScotPart1ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_1() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_2() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_1() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "1", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_2() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "2", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "3", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_4() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "4", true);
    }

    @Test
    public void generateDocument_Part_Scot_3_5() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "5", true);
    }

    @Test
    public void generateDocument_Part_Scot_4() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_5() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_6() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_7() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_1() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "1", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_2() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "2", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_3() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "3", true);
    }

    @Test
    public void generateDocument_Part_Scot_7_5() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", true);
    }

    @After
    public void tearDown() {

    }
}
