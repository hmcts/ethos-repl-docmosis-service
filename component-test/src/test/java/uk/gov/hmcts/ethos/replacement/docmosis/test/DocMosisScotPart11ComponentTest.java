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
public class DocMosisScotPart11ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_92() throws Exception {
        testUtil.executeGenerateDocumentTest("92", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_92A() throws Exception {
        testUtil.executeGenerateDocumentTest("92A", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_92B() throws Exception {
        testUtil.executeGenerateDocumentTest("92B", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_92C() throws Exception {
        testUtil.executeGenerateDocumentTest("92C", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_92D() throws Exception {
        testUtil.executeGenerateDocumentTest("92D", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_92E() throws Exception {
        testUtil.executeGenerateDocumentTest("92E", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_93() throws Exception {
        testUtil.executeGenerateDocumentTest("93", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_93A() throws Exception {
        testUtil.executeGenerateDocumentTest("93A", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_93B() throws Exception {
        testUtil.executeGenerateDocumentTest("93B", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_94() throws Exception {
        testUtil.executeGenerateDocumentTest("94", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_95() throws Exception {
        testUtil.executeGenerateDocumentTest("95", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_96() throws Exception {
        testUtil.executeGenerateDocumentTest("96", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_97() throws Exception {
        testUtil.executeGenerateDocumentTest("97", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_98() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "", true);
    }


    @After
    public void tearDown() {

    }
}
