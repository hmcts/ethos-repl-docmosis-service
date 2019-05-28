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
public class DocMosisPart16ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part16_1() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "1", "");
    }

    @Test
    public void generateDocument_Part16_2() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "2", "");
    }

    @Test
    public void generateDocument_Part16_3() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "3", "");
    }

    @Test
    public void generateDocument_Part16_4() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "4", "");
    }

    @Test
    public void generateDocument_Part16_5() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "", "");
    }

    @After
    public void tearDown() {

    }
}
