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
public class DocMosisScotPart16ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_180() throws Exception {
        testUtil.executeGenerateDocumentTest("180", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_181() throws Exception {
        testUtil.executeGenerateDocumentTest("181", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_182() throws Exception {
        testUtil.executeGenerateDocumentTest("182", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_183() throws Exception {
        testUtil.executeGenerateDocumentTest("183", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_184() throws Exception {
        testUtil.executeGenerateDocumentTest("184", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_185() throws Exception {
        testUtil.executeGenerateDocumentTest("185", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_186() throws Exception {
        testUtil.executeGenerateDocumentTest("186", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_189() throws Exception {
        testUtil.executeGenerateDocumentTest("189", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_190() throws Exception {
        testUtil.executeGenerateDocumentTest("190", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_191() throws Exception {
        testUtil.executeGenerateDocumentTest("191", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_192() throws Exception {
        testUtil.executeGenerateDocumentTest("192", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_193() throws Exception {
        testUtil.executeGenerateDocumentTest("193", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_194() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "", true);
    }

    @After
    public void tearDown() {

    }
}
