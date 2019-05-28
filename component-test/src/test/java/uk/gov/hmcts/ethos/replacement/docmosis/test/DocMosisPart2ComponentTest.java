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
public class DocMosisPart2ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part1_1() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "1", "");
    }

    @Test
    public void generateDocument_Part1_2() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "2", "");
    }

    @Test
    public void generateDocument_Part1_3() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "3", "");
    }

    @Test
    public void generateDocument_Part1_4() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "4", "");
    }

    @Test
    public void generateDocument_Part1_5() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "5", "");
    }

    @Test
    public void generateDocument_Part1_6() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "6", "");
    }

    @Test
    public void generateDocument_Part1_6A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "6A", "");
    }

    @Test
    public void generateDocument_Part1_7() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "7", "");
    }

    @Test
    public void generateDocument_Part1_8() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "8", "");
    }

    @Test
    public void generateDocument_Part1_9() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "9", "");
    }

    @Test
    public void generateDocument_Part1_10() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "10", "");
    }

    @Test
    public void generateDocument_Part1_10A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "10A", "");
    }

    @Test
    public void generateDocument_Part1_11() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "11", "");
    }

    @Test
    public void generateDocument_Part1_11A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "11A", "");
    }

    @Test
    public void generateDocument_Part1_12() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "12", "");
    }

    @Test
    public void generateDocument_Part1_13() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "13", "");
    }

    @Test
    public void generateDocument_Part1_14() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "14", "");
    }

    @Test
    public void generateDocument_Part1_15() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "15", "");
    }

    @Test
    public void generateDocument_Part1_16() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "16", "");
    }

    @Test
    public void generateDocument_Part1_17() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "17", "");
    }

    @Test
    public void generateDocument_Part1_18() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "18", "");
    }

    @Test
    public void generateDocument_Part1_18A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "18A", "");
    }

    @Test
    public void generateDocument_Part1_19() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "19", "");
    }

    @Test
    public void generateDocument_Part1_20() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "20", "");
    }

    @Test
    public void generateDocument_Part1_21() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "21", "");
    }

    @Test
    public void generateDocument_Part1_22() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "22", "");
    }

    @Test
    public void generateDocument_Part1_23() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "23", "");
    }

    @Test
    public void generateDocument_Part1_24() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "24", "");
    }

    @Test
    public void generateDocument_Part1_25() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "25", "");
    }

    @Test
    public void generateDocument_Part1_26() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "26", "");
    }

    @Test
    public void generateDocument_Part1_26A() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "26A", "");
    }

    @Test
    public void generateDocument_Part1_27() throws Exception {
        testUtil.executeGenerateDocumentTest("2", "", "");
    }

    @After
    public void tearDown() {

    }
}
