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
public class DocMosisPart14ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part14_1() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "1");
    }

    @Test
    public void generateDocument_Part14_2() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "2");
    }

    @Test
    public void generateDocument_Part14_3() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "3");
    }

    @Test
    public void generateDocument_Part14_4() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "4");
    }

    @Test
    public void generateDocument_Part14_5() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "5");
    }

    @Test
    public void generateDocument_Part14_6() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "6");
    }

    @Test
    public void generateDocument_Part14_7() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "7");
    }

    @Test
    public void generateDocument_Part14_8() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "8");
    }

    @Test
    public void generateDocument_Part14_9() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "9");
    }

    @Test
    public void generateDocument_Part14_10() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "10");
    }

    @After
    public void tearDown() {

    }
}
