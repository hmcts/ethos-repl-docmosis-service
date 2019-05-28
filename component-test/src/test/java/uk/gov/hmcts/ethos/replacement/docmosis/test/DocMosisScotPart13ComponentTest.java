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
public class DocMosisScotPart13ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_104() throws Exception {
        testUtil.executeGenerateDocumentTest("104", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_105() throws Exception {
        testUtil.executeGenerateDocumentTest("105", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_105A() throws Exception {
        testUtil.executeGenerateDocumentTest("105A", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_106() throws Exception {
        testUtil.executeGenerateDocumentTest("106", "", "", true);
    }

    @Test
    public void generateDocument_Part_Scot_106A() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "", true);
    }


    @After
    public void tearDown() {

    }
}
