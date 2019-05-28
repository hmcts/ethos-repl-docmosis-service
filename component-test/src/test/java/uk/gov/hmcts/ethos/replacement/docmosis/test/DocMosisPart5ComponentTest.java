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
public class DocMosisPart5ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part5_1A() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1A", "");
    }

    @Test
    public void generateDocument_Part5_1B() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1B", "");
    }

    @Test
    public void generateDocument_Part5_1C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1C", "");
    }

    @Test
    public void generateDocument_Part5_1R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "1R", "");
    }

    @Test
    public void generateDocument_Part5_2C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "2C", "");
    }

    @Test
    public void generateDocument_Part5_2R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "2R", "");
    }

    @Test
    public void generateDocument_Part5_3C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "3C", "");
    }

    @Test
    public void generateDocument_Part5_3R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "3R", "");
    }

    @Test
    public void generateDocument_Part5_4C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "4C", "");
    }

    @Test
    public void generateDocument_Part5_4R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "4R", "");
    }

    @Test
    public void generateDocument_Part5_5() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "5", "");
    }

    @Test
    public void generateDocument_Part5_5A() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "5A", "");
    }

    @Test
    public void generateDocument_Part5_6() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "6", "");
    }

    @Test
    public void generateDocument_Part5_7() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "7", "");
    }

    @Test
    public void generateDocument_Part5_8() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "8", "");
    }

    @Test
    public void generateDocument_Part5_9C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "9C", "");
    }

    @Test
    public void generateDocument_Part5_9R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "9R", "");
    }

    @Test
    public void generateDocument_Part5_10() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "10", "");
    }

    @Test
    public void generateDocument_Part5_11C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "11C", "");
    }

    @Test
    public void generateDocument_Part5_11R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "11R", "");
    }

    @Test
    public void generateDocument_Part5_12C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "12C", "");
    }

    @Test
    public void generateDocument_Part5_12R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "12R", "");
    }

    @Test
    public void generateDocument_Part5_13C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "13C", "");
    }

    @Test
    public void generateDocument_Part5_13R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "13R", "");
    }

    @Test
    public void generateDocument_Part5_14C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "14C", "");
    }

    @Test
    public void generateDocument_Part5_14R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "14R", "");
    }

    @Test
    public void generateDocument_Part5_15C() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "15C", "");
    }

    @Test
    public void generateDocument_Part5_15R() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "15R", "");
    }

    @Test
    public void generateDocument_Part5_16() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "16", "");
    }

    @Test
    public void generateDocument_Part5_17() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "17", "");
    }

    @Test
    public void generateDocument_Part5_18() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "18", "");
    }

    @Test
    public void generateDocument_Part5_19() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "19", "");
    }

    @Test
    public void generateDocument_Part5_19A() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "19A", "");
    }

    @Test
    public void generateDocument_Part5_20() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "20", "");
    }

    @Test
    public void generateDocument_Part5_21() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "21", "");
    }

    @Test
    public void generateDocument_Part5_22() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "22", "");
    }

    @Test
    public void generateDocument_Part5_23() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "23", "");
    }

    @Test
    public void generateDocument_Part5_24() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "24", "");
    }

    @Test
    public void generateDocument_Part5_25() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "25", "");
    }

    @Test
    public void generateDocument_Part5_26() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "26", "");
    }

    @Test
    public void generateDocument_Part5_27() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "27", "");
    }

    @Test
    public void generateDocument_Part5_28() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "28", "");
    }

    @Test
    public void generateDocument_Part5_29() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "29", "");
    }

    @Test
    public void generateDocument_Part5_30() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "30", "");
    }

    @Test
    public void generateDocument_Part5_31() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "31", "");
    }

    @Test
    public void generateDocument_Part5_32() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "32", "");
    }

    @Test
    public void generateDocument_Part5_33() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "33", "");
    }

    @Test
    public void generateDocument_Part5_34() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "34", "");
    }

    @Test
    public void generateDocument_Part5_35() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "35", "");
    }

    @Test
    public void generateDocument_Part5_36() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "36", "");
    }

    @Test
    public void generateDocument_Part5_37() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "37", "");
    }

    @Test
    public void generateDocument_Part5_38() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "38", "");
    }

    @Test
    public void generateDocument_Part5_39() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "39", "");
    }

    @Test
    public void generateDocument_Part5_40() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "40", "");
    }

    @Test
    public void generateDocument_Part5_41() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "41", "");
    }

    @Test
    public void generateDocument_Part5_42() throws Exception {
        testUtil.executeGenerateDocumentTest("5", "", "");
    }

    @After
    public void tearDown() {

    }
}
