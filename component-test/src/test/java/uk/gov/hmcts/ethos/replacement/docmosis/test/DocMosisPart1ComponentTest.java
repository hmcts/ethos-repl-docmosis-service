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
public class DocMosisPart1ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part1_1() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "1");
    }

    @Test
    public void generateDocument_Part1_1A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "1A");
    }

    @Test
    public void generateDocument_Part1_2() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "2");
    }

    @Test
    public void generateDocument_Part1_3() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "3");
    }

    @Test
    public void generateDocument_Part1_3A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "3A");
    }

    @Test
    public void generateDocument_Part1_3B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "3B");
    }

    @Test
    public void generateDocument_Part1_4() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "4");
    }

    @Test
    public void generateDocument_Part1_5() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "5");
    }

    @Test
    public void generateDocument_Part1_6() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "6");
    }

    @Test
    public void generateDocument_Part1_6A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "6A");
    }

    @Test
    public void generateDocument_Part1_6B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "6B");
    }

    @Test
    public void generateDocument_Part1_7() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "7");
    }

    @Test
    public void generateDocument_Part1_8() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "8");
    }

    @Test
    public void generateDocument_Part1_9() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9");
    }

    @Test
    public void generateDocument_Part1_9A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9A");
    }

    @Test
    public void generateDocument_Part1_9B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9B");
    }

    @Test
    public void generateDocument_Part1_9C() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "9C");
    }

    @Test
    public void generateDocument_Part1_10() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "10");
    }

    @Test
    public void generateDocument_Part1_11() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "11");
    }

    @Test
    public void generateDocument_Part1_11A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "11A");
    }

    @Test
    public void generateDocument_Part1_12() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "12");
    }

    @Test
    public void generateDocument_Part1_13() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "13");
    }

    @Test
    public void generateDocument_Part1_14() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "14");
    }

    @Test
    public void generateDocument_Part1_14A() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "14A");
    }

    @Test
    public void generateDocument_Part1_14B() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "14B");
    }

    @Test
    public void generateDocument_Part1_15() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "15");
    }

    @Test
    public void generateDocument_Part1_16() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "16");
    }

    @Test
    public void generateDocument_Part1_17() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "17");
    }

    @Test
    public void generateDocument_Part1_18() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "18");
    }

    @Test
    public void generateDocument_Part1_19() throws Exception {
        testUtil.executeGenerateDocumentTest("1", "");
    }

    @After
    public void tearDown() {

    }
}
