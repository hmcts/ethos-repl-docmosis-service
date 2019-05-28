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
public class DocMosisPart3ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part3_1() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "1", "");
    }

    @Test
    public void generateDocument_Part3_2() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "2", "");
    }

    @Test
    public void generateDocument_Part3_3() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "3", "");
    }

    @Test
    public void generateDocument_Part3_4() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "4", "");
    }

    @Test
    public void generateDocument_Part3_5() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "5", "");
    }

    @Test
    public void generateDocument_Part3_6() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "6", "");
    }

    @Test
    public void generateDocument_Part3_7() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "7", "");
    }

    @Test
    public void generateDocument_Part3_8() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "8", "");
    }

    @Test
    public void generateDocument_Part3_9() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "9", "");
    }

    @Test
    public void generateDocument_Part3_10() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "10", "");
    }

    @Test
    public void generateDocument_Part3_11() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "11", "");
    }

    @Test
    public void generateDocument_Part3_12() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "12", "");
    }

    @Test
    public void generateDocument_Part3_13() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "13", "");
    }

    @Test
    public void generateDocument_Part3_14() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "14", "");
    }

    @Test
    public void generateDocument_Part3_15() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "15", "");
    }

    @Test
    public void generateDocument_Part3_16() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "16", "");
    }

    @Test
    public void generateDocument_Part3_17() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "17", "");
    }

    @Test
    public void generateDocument_Part3_18() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "18", "");
    }

    @Test
    public void generateDocument_Part3_19() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "19", "");
    }

    @Test
    public void generateDocument_Part3_20() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "20", "");
    }

    @Test
    public void generateDocument_Part3_21() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "21", "");
    }

    @Test
    public void generateDocument_Part3_22() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "22", "");
    }

    @Test
    public void generateDocument_Part3_23() throws Exception {
        testUtil.executeGenerateDocumentTest("3", "", "");
    }

    @After
    public void tearDown() {

    }
}
