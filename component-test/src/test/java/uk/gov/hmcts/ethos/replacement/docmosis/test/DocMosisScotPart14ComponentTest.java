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
public class DocMosisScotPart14ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_110() throws Exception {
        testUtil.executeGenerateDocumentTest("110", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_110A() throws Exception {
        testUtil.executeGenerateDocumentTest("110A", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_111() throws Exception {
        testUtil.executeGenerateDocumentTest("111", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_112() throws Exception {
        testUtil.executeGenerateDocumentTest("112", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_113() throws Exception {
        testUtil.executeGenerateDocumentTest("113", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_114() throws Exception {
        testUtil.executeGenerateDocumentTest("114", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_115() throws Exception {
        testUtil.executeGenerateDocumentTest("115", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_116() throws Exception {
        testUtil.executeGenerateDocumentTest("116", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_117() throws Exception {
        testUtil.executeGenerateDocumentTest("117", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_118() throws Exception {
        testUtil.executeGenerateDocumentTest("118", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_119C() throws Exception {
        testUtil.executeGenerateDocumentTest("119C", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_119R() throws Exception {
        testUtil.executeGenerateDocumentTest("119R", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_120() throws Exception {
        testUtil.executeGenerateDocumentTest("120", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_121() throws Exception {
        testUtil.executeGenerateDocumentTest("121", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_122() throws Exception {
        testUtil.executeGenerateDocumentTest("122", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_123C() throws Exception {
        testUtil.executeGenerateDocumentTest("123C", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_123R() throws Exception {
        testUtil.executeGenerateDocumentTest("123R", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_124C() throws Exception {
        testUtil.executeGenerateDocumentTest("124C", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_124R() throws Exception {
        testUtil.executeGenerateDocumentTest("124R", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_125C() throws Exception {
        testUtil.executeGenerateDocumentTest("125C", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_125R() throws Exception {
        testUtil.executeGenerateDocumentTest("125R", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_126() throws Exception {
        testUtil.executeGenerateDocumentTest("126", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_127() throws Exception {
        testUtil.executeGenerateDocumentTest("127", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_128() throws Exception {
        testUtil.executeGenerateDocumentTest("128", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_129C() throws Exception {
        testUtil.executeGenerateDocumentTest("129C", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_129R() throws Exception {
        testUtil.executeGenerateDocumentTest("129R", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_130() throws Exception {
        testUtil.executeGenerateDocumentTest("130", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_131() throws Exception {
        testUtil.executeGenerateDocumentTest("131", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_132() throws Exception {
        testUtil.executeGenerateDocumentTest("132", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_133() throws Exception {
        testUtil.executeGenerateDocumentTest("133", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_134() throws Exception {
        testUtil.executeGenerateDocumentTest("134", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_135() throws Exception {
        testUtil.executeGenerateDocumentTest("135", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_136() throws Exception {
        testUtil.executeGenerateDocumentTest("136", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_137() throws Exception {
        testUtil.executeGenerateDocumentTest("137", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_138() throws Exception {
        testUtil.executeGenerateDocumentTest("138", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_139() throws Exception {
        testUtil.executeGenerateDocumentTest("139", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_140() throws Exception {
        testUtil.executeGenerateDocumentTest("140", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_141() throws Exception {
        testUtil.executeGenerateDocumentTest("141", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_142() throws Exception {
        testUtil.executeGenerateDocumentTest("142", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_143() throws Exception {
        testUtil.executeGenerateDocumentTest("143", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_144() throws Exception {
        testUtil.executeGenerateDocumentTest("144", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_145() throws Exception {
        testUtil.executeGenerateDocumentTest("145", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_146() throws Exception {
        testUtil.executeGenerateDocumentTest("146", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_147() throws Exception {
        testUtil.executeGenerateDocumentTest("147", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_148() throws Exception {
        testUtil.executeGenerateDocumentTest("148", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_149() throws Exception {
        testUtil.executeGenerateDocumentTest("149", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_150() throws Exception {
        testUtil.executeGenerateDocumentTest("150", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_151() throws Exception {
        testUtil.executeGenerateDocumentTest("151", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_152() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "", true);
    }

    @After
    public void tearDown() {

    }
}
