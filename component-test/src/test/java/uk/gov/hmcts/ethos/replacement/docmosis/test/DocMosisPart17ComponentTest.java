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
public class DocMosisPart17ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part17_1() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "1", "");
    }

    @Test
    public void generateDocument_Part17_2() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "2", "");
    }

    @Test
    public void generateDocument_Part17_3() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "3", "");
    }

    @Test
    public void generateDocument_Part17_4() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "4", "");
    }

    @Test
    public void generateDocument_Part17_5() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "5", "");
    }

    @Test
    public void generateDocument_Part17_6() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "6", "");
    }

    @Test
    public void generateDocument_Part17_7() throws Exception {
        testUtil.executeGenerateDocumentTest("17", "", "");
    }

    @After
    public void tearDown() {

    }
}
