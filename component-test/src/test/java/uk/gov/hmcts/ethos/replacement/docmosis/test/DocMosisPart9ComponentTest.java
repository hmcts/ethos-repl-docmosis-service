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
public class DocMosisPart9ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part9_1() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "1", "");
    }

    @Test
    public void generateDocument_Part9_1B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "1B", "");
    }

    @Test
    public void generateDocument_Part9_2A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "2A", "");
    }

    @Test
    public void generateDocument_Part9_2B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "2B", "");
    }

    @Test
    public void generateDocument_Part9_3A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "3A", "");
    }

    @Test
    public void generateDocument_Part9_3B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "3B", "");
    }

    @Test
    public void generateDocument_Part9_4() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "4", "");
    }

    @Test
    public void generateDocument_Part9_5() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "5", "");
    }

    @Test
    public void generateDocument_Part9_6A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "6A", "");
    }

    @Test
    public void generateDocument_Part9_6B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "6B", "");
    }

    @Test
    public void generateDocument_Part9_7A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "7A", "");
    }

    @Test
    public void generateDocument_Part9_7B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "7B", "");
    }

    @Test
    public void generateDocument_Part9_8A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "8A", "");
    }

    @Test
    public void generateDocument_Part9_8B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "8B", "");
    }

    @Test
    public void generateDocument_Part9_9() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "", "");
    }

    @After
    public void tearDown() {

    }
}
