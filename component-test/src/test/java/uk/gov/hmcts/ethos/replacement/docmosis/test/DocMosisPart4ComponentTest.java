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
public class DocMosisPart4ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part4_1() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "1", "");
    }

    @Test
    public void generateDocument_Part4_2() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "2", "");
    }

    @Test
    public void generateDocument_Part4_3() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "3", "");
    }

    @Test
    public void generateDocument_Part4_4() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "4", "");
    }

    @Test
    public void generateDocument_Part4_5() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "5", "");
    }

    @Test
    public void generateDocument_Part4_6() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "6", "");
    }

    @Test
    public void generateDocument_Part4_7() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "7", "");
    }

    @Test
    public void generateDocument_Part4_8() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "8", "");
    }

    @Test
    public void generateDocument_Part4_8A() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "8A", "");
    }

    @Test
    public void generateDocument_Part4_9() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "9", "");
    }

    @Test
    public void generateDocument_Part4_9A() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "9A", "");
    }

    @Test
    public void generateDocument_Part4_10() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "10", "");
    }

    @Test
    public void generateDocument_Part4_11() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "11", "");
    }

    @Test
    public void generateDocument_Part4_12() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "12", "");
    }

    @Test
    public void generateDocument_Part4_13() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "13", "");
    }

    @Test
    public void generateDocument_Part4_14() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "14", "");
    }

    @Test
    public void generateDocument_Part4_15() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "15", "");
    }

    @Test
    public void generateDocument_Part4_16() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "16", "");
    }

    @Test
    public void generateDocument_Part4_17() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "17", "");
    }

    @Test
    public void generateDocument_Part4_18() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "18", "");
    }

    @Test
    public void generateDocument_Part4_19() throws Exception {
        testUtil.executeGenerateDocumentTest("4", "", "");
    }

    @After
    public void tearDown() {

    }
}
