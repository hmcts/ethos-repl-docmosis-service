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
public class DocMosisPart12ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part12_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "1C", "");
    }

    @Test
    public void generateDocument_Part12_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "1R", "");
    }

    @Test
    public void generateDocument_Part12_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "2C", "");
    }

    @Test
    public void generateDocument_Part12_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "2R", "");
    }

    @Test
    public void generateDocument_Part12_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "3C", "");
    }

    @Test
    public void generateDocument_Part12_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "3R", "");
    }

    @Test
    public void generateDocument_Part12_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "4C", "");
    }

    @Test
    public void generateDocument_Part12_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "4R", "");
    }

    @Test
    public void generateDocument_Part12_5C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "5C", "");
    }

    @Test
    public void generateDocument_Part12_5R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "5R", "");
    }

    @Test
    public void generateDocument_Part12_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "6C", "");
    }

    @Test
    public void generateDocument_Part12_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "6R", "");
    }

    @Test
    public void generateDocument_Part12_7C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "7C", "");
    }

    @Test
    public void generateDocument_Part12_7R() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "7R", "");
    }

    @Test
    public void generateDocument_Part12_8C() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "8C", "");
    }

    @Test
    public void generateDocument_Part12_9() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "", "");
    }

    @After
    public void tearDown() {

    }
}
