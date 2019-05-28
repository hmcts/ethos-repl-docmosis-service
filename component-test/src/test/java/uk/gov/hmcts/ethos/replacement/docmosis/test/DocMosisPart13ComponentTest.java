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
public class DocMosisPart13ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part13_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "1C", "");
    }

    @Test
    public void generateDocument_Part13_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "1R", "");
    }

    @Test
    public void generateDocument_Part13_2() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "2", "");
    }

    @Test
    public void generateDocument_Part13_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "2C", "");
    }

    @Test
    public void generateDocument_Part13_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "2R", "");
    }

    @Test
    public void generateDocument_Part13_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "3C", "");
    }

    @Test
    public void generateDocument_Part13_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "3R", "");
    }

    @Test
    public void generateDocument_Part13_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "4C", "");
    }

    @Test
    public void generateDocument_Part13_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "4R", "");
    }

    @Test
    public void generateDocument_Part13_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "5C", "");
    }

    @Test
    public void generateDocument_Part13_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "5R", "");
    }

    @Test
    public void generateDocument_Part13_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "6C", "");
    }

    @Test
    public void generateDocument_Part13_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "6R", "");
    }

    @Test
    public void generateDocument_Part13_7C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "7C", "");
    }

    @Test
    public void generateDocument_Part13_7R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "7R", "");
    }

    @Test
    public void generateDocument_Part13_8C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "8C", "");
    }

    @Test
    public void generateDocument_Part13_8R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "8R", "");
    }

    @Test
    public void generateDocument_Part13_9C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "9C", "");
    }

    @Test
    public void generateDocument_Part13_9R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "9R", "");
    }

    @Test
    public void generateDocument_Part13_10C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "10C", "");
    }

    @Test
    public void generateDocument_Part13_10R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "10R", "");
    }

    @Test
    public void generateDocument_Part13_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "11C", "");
    }

    @Test
    public void generateDocument_Part13_11R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "11R", "");
    }

    @Test
    public void generateDocument_Part13_12C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "12C", "");
    }

    @Test
    public void generateDocument_Part13_12R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "12R", "");
    }

    @Test
    public void generateDocument_Part13_13() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "13", "");
    }

    @Test
    public void generateDocument_Part13_14C() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "14C", "");
    }

    @Test
    public void generateDocument_Part13_14R() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "14R", "");
    }

    @Test
    public void generateDocument_Part13_15() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "15", "");
    }

    @Test
    public void generateDocument_Part13_16() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "16", "");
    }

    @Test
    public void generateDocument_Part13_17() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "17", "");
    }

    @Test
    public void generateDocument_Part13_18() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "18", "");
    }

    @Test
    public void generateDocument_Part13_19() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "19", "");
    }

    @Test
    public void generateDocument_Part13_20() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "", "");
    }

    @After
    public void tearDown() {

    }
}
