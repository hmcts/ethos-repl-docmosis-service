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
public class DocMosisScotPart2ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_8() throws Exception {
        testUtil.executeGenerateDocumentTest("8", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_9() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_10() throws Exception {
        testUtil.executeGenerateDocumentTest("10", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_11() throws Exception {
        testUtil.executeGenerateDocumentTest("11", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_12() throws Exception {
        testUtil.executeGenerateDocumentTest("12", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_13() throws Exception {
        testUtil.executeGenerateDocumentTest("13", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_14() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_14_1() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", true);
    }

    @After
    public void tearDown() {

    }
}
