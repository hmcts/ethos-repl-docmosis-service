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
public class DocMosisPart7ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part7_1() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "1", "");
    }

    @Test
    public void generateDocument_Part7_2() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "2", "");
    }

    @Test
    public void generateDocument_Part7_3() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "3", "");
    }

    @Test
    public void generateDocument_Part7_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "4C", "");
    }

    @Test
    public void generateDocument_Part7_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "4R", "");
    }

    @Test
    public void generateDocument_Part7_5() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "5", "");
    }

    @Test
    public void generateDocument_Part7_6C() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "6C", "");
    }

    @Test
    public void generateDocument_Part7_6R() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "6R", "");
    }

    @Test
    public void generateDocument_Part7_7() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "7", "");
    }

    @Test
    public void generateDocument_Part7_8() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "8", "");
    }

    @Test
    public void generateDocument_Part7_9() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "9", "");
    }

    @Test
    public void generateDocument_Part7_10() throws Exception {
        testUtil.executeGenerateDocumentTest("7", "", "");
    }

    @After
    public void tearDown() {

    }
}
