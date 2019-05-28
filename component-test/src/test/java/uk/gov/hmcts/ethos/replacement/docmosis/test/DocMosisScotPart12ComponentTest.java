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
public class DocMosisScotPart12ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_99() throws Exception {
        testUtil.executeGenerateDocumentTest("99", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_100() throws Exception {
        testUtil.executeGenerateDocumentTest("100", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_101() throws Exception {
        testUtil.executeGenerateDocumentTest("101", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_102() throws Exception {
        testUtil.executeGenerateDocumentTest("102", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_103() throws Exception {
        testUtil.executeGenerateDocumentTest("103", "", "", true);
    }

    @After
    public void tearDown() {

    }
}
