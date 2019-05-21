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
public class DocMosisScotPart5ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_26() throws Exception {
        testUtil.executeGenerateDocumentTest("26", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_27() throws Exception {
        testUtil.executeGenerateDocumentTest("27", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_28() throws Exception {
        testUtil.executeGenerateDocumentTest("28", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_29() throws Exception {
        testUtil.executeGenerateDocumentTest("29", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_30() throws Exception {
        testUtil.executeGenerateDocumentTest("30", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_31() throws Exception {
        testUtil.executeGenerateDocumentTest("31", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_32() throws Exception {
        testUtil.executeGenerateDocumentTest("32", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_33() throws Exception {
        testUtil.executeGenerateDocumentTest("33", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_33_1() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", true);
    }

    @After
    public void tearDown() {

    }
}
