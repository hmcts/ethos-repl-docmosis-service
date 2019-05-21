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
public class DocMosisPart6ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part6_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "1C");
    }

    @Test
    public void generateDocument_Part6_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "1R");
    }

    @Test
    public void generateDocument_Part6_2() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "2");
    }

    @Test
    public void generateDocument_Part6_3() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "3");
    }

    @Test
    public void generateDocument_Part6_4() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "4");
    }

    @Test
    public void generateDocument_Part6_5() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "5");
    }

    @Test
    public void generateDocument_Part6_6() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "6");
    }

    @Test
    public void generateDocument_Part6_7() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "7");
    }

    @Test
    public void generateDocument_Part6_8() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "8");
    }

    @Test
    public void generateDocument_Part6_9() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "9");
    }

    @Test
    public void generateDocument_Part6_10() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "10");
    }

    @Test
    public void generateDocument_Part6_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "11C");
    }

    @Test
    public void generateDocument_Part6_12() throws Exception {
        testUtil.executeGenerateDocumentTest("6", "");
    }

    @After
    public void tearDown() {

    }
}
