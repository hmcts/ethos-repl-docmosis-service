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
        testUtil.executeGenerateDocumentTest("10", "1", "");
    }

    @Test
    public void generateDocument_Part10_2() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "2", "");
    }

    @Test
    public void generateDocument_Part10_3() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "3", "");
    }

    @Test
    public void generateDocument_Part10_4() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "4", "");
    }

    @Test
    public void generateDocument_Part10_5() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "5", "");
    }

    @Test
    public void generateDocument_Part10_6() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "6", "");
    }

    @Test
    public void generateDocument_Part10_7() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "7", "");
    }

    @Test
    public void generateDocument_Part10_8() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "8", "");
    }

    @Test
    public void generateDocument_Part10_9() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "9", "");
    }

    @Test
    public void generateDocument_Part10_10() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "10", "");
    }

    @Test
    public void generateDocument_Part10_11() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "11", "");
    }

    @Test
    public void generateDocument_Part10_12() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "12", "");
    }

    @Test
    public void generateDocument_Part10_13() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "13", "");
    }

    @Test
    public void generateDocument_Part10_14() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "14", "");
    }

    @Test
    public void generateDocument_Part10_15() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "15", "");
    }

    @Test
    public void generateDocument_Part10_16() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "", "");
    }

    @After
    public void tearDown() {

    }
}
