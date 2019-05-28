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
        testUtil.executeGenerateDocumentTest("11", "1C", "");
    }

    @Test
    public void generateDocument_Part11_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "1R", "");
    }

    @Test
    public void generateDocument_Part11_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "2C", "");
    }

    @Test
    public void generateDocument_Part11_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "2R", "");
    }

    @Test
    public void generateDocument_Part11_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "3C", "");
    }

    @Test
    public void generateDocument_Part11_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "3R", "");
    }

    @Test
    public void generateDocument_Part11_4() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "4", "");
    }

    @Test
    public void generateDocument_Part11_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "5C", "");
    }

    @Test
    public void generateDocument_Part11_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "5R", "");
    }

    @Test
    public void generateDocument_Part11_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "6C", "");
    }

    @Test
    public void generateDocument_Part11_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "6R", "");
    }

    @Test
    public void generateDocument_Part11_7() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "7", "");
    }

    @Test
    public void generateDocument_Part11_8() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "8", "");
    }

    @Test
    public void generateDocument_Part11_9() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "9", "");
    }

    @Test
    public void generateDocument_Part11_10() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "10", "");
    }

    @Test
    public void generateDocument_Part11_11() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "11", "");
    }

    @Test
    public void generateDocument_Part11_12() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "12", "");
    }

    @Test
    public void generateDocument_Part11_13() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "", "");
    }

    @After
    public void tearDown() {

    }
}
