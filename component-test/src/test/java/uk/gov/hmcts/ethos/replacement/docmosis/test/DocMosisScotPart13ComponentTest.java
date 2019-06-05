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
public class DocMosisScotPart13ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_104() throws Exception {
        testUtil.executeGenerateDocumentTest("104", "", "WASTED COSTS ORDER REFUSED", true);
    }

    @Test
    public void generateDocument_Part_Scot_105() throws Exception {
        testUtil.executeGenerateDocumentTest("105", "", "APPLICATION FOR AN EXPENSES ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_105A() throws Exception {
        testUtil.executeGenerateDocumentTest("105", "A", "EXPENSES ORDER/PREPARATION TIME ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_106() throws Exception {
        testUtil.executeGenerateDocumentTest("106", "", "APPLICATION FOR A WASTED COSTS ORDER", true);
    }

    @Test
    public void generateDocument_Part_Scot_106_A() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "An Employment Judge is considering making a wasted costs against you in favour of", true);
    }


    @After
    public void tearDown() {

    }
}
