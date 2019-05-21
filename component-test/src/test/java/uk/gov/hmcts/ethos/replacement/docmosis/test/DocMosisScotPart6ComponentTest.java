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
public class DocMosisScotPart6ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_34() throws Exception {
        testUtil.executeGenerateDocumentTest("34", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_34A() throws Exception {
        testUtil.executeGenerateDocumentTest("34A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_35() throws Exception {
        testUtil.executeGenerateDocumentTest("35", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_35A() throws Exception {
        testUtil.executeGenerateDocumentTest("35A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_36() throws Exception {
        testUtil.executeGenerateDocumentTest("36", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_36A() throws Exception {
        testUtil.executeGenerateDocumentTest("36A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_37() throws Exception {
        testUtil.executeGenerateDocumentTest("37", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_37A() throws Exception {
        testUtil.executeGenerateDocumentTest("37A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_38() throws Exception {
        testUtil.executeGenerateDocumentTest("38", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_38A() throws Exception {
        testUtil.executeGenerateDocumentTest("38A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_39() throws Exception {
        testUtil.executeGenerateDocumentTest("39", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_40() throws Exception {
        testUtil.executeGenerateDocumentTest("40", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_41() throws Exception {
        testUtil.executeGenerateDocumentTest("41", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_42() throws Exception {
        testUtil.executeGenerateDocumentTest("42", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_43() throws Exception {
        testUtil.executeGenerateDocumentTest("43", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_44() throws Exception {
        testUtil.executeGenerateDocumentTest("44", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_45() throws Exception {
        testUtil.executeGenerateDocumentTest("45", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_46() throws Exception {
        testUtil.executeGenerateDocumentTest("46", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_47() throws Exception {
        testUtil.executeGenerateDocumentTest("47", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_48() throws Exception {
        testUtil.executeGenerateDocumentTest("48", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_49() throws Exception {
        testUtil.executeGenerateDocumentTest("49", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_50() throws Exception {
        testUtil.executeGenerateDocumentTest("50", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_50A() throws Exception {
        testUtil.executeGenerateDocumentTest("50A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_51() throws Exception {
        testUtil.executeGenerateDocumentTest("51", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_52() throws Exception {
        testUtil.executeGenerateDocumentTest("52", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_53() throws Exception {
        testUtil.executeGenerateDocumentTest("53", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_54() throws Exception {
        testUtil.executeGenerateDocumentTest("54", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_55() throws Exception {
        testUtil.executeGenerateDocumentTest("55", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_56() throws Exception {
        testUtil.executeGenerateDocumentTest("56", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_57() throws Exception {
        testUtil.executeGenerateDocumentTest("57", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_57A() throws Exception {
        testUtil.executeGenerateDocumentTest("57A", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_57B() throws Exception {
        testUtil.executeGenerateDocumentTest("57B", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_57C() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", true);
    }

    @After
    public void tearDown() {

    }
}
