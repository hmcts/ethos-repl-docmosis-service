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
public class DocMosisScotPart10ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_71() throws Exception {
        testUtil.executeGenerateDocumentTest("71", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_72() throws Exception {
        testUtil.executeGenerateDocumentTest("72", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_73() throws Exception {
        testUtil.executeGenerateDocumentTest("73", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_74() throws Exception {
        testUtil.executeGenerateDocumentTest("74", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_75() throws Exception {
        testUtil.executeGenerateDocumentTest("75", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_76() throws Exception {
        testUtil.executeGenerateDocumentTest("76", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_78() throws Exception {
        testUtil.executeGenerateDocumentTest("78", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_79() throws Exception {
        testUtil.executeGenerateDocumentTest("79", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_80() throws Exception {
        testUtil.executeGenerateDocumentTest("80", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_81() throws Exception {
        testUtil.executeGenerateDocumentTest("81", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_82() throws Exception {
        testUtil.executeGenerateDocumentTest("82", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_83() throws Exception {
        testUtil.executeGenerateDocumentTest("83", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_84() throws Exception {
        testUtil.executeGenerateDocumentTest("84", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_85() throws Exception {
        testUtil.executeGenerateDocumentTest("85", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_86() throws Exception {
        testUtil.executeGenerateDocumentTest("86", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_87() throws Exception {
        testUtil.executeGenerateDocumentTest("87", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_90() throws Exception {
        testUtil.executeGenerateDocumentTest("90", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_91() throws Exception {
        testUtil.executeGenerateDocumentTest("91", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_91B() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", true);
    }

    @After
    public void tearDown() {

    }
}
