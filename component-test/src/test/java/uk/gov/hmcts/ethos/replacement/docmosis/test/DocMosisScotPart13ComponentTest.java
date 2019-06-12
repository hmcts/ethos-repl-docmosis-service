package uk.gov.hmcts.ethos.replacement.docmosis.test;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.TestUtil;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
public class DocMosisScotPart13ComponentTest {


    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
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
