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
public class DocMosisScotPart15ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_159() throws Exception {
        testUtil.executeGenerateDocumentTest("159", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_160() throws Exception {
        testUtil.executeGenerateDocumentTest("160", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_161() throws Exception {
        testUtil.executeGenerateDocumentTest("161", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_162() throws Exception {
        testUtil.executeGenerateDocumentTest("162", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_163() throws Exception {
        testUtil.executeGenerateDocumentTest("163", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_164() throws Exception {
        testUtil.executeGenerateDocumentTest("164", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_165() throws Exception {
        testUtil.executeGenerateDocumentTest("165", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_166() throws Exception {
        testUtil.executeGenerateDocumentTest("166", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_167() throws Exception {
        testUtil.executeGenerateDocumentTest("167", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_168() throws Exception {
        testUtil.executeGenerateDocumentTest("168", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_169() throws Exception {
        testUtil.executeGenerateDocumentTest("169", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_170() throws Exception {
        testUtil.executeGenerateDocumentTest("170", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_171() throws Exception {
        testUtil.executeGenerateDocumentTest("171", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_172() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "", true);
    }

    @After
    public void tearDown() {

    }
}
