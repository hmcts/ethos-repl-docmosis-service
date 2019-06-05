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
public class DocMosisScotPart3ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_15() throws Exception {
        testUtil.executeGenerateDocumentTest("15", "", "your claim - FURTHER INFORMATION REQUIRED", true);
    }

    @Test
    public void generateDocument_Part_Scot_16() throws Exception {
        testUtil.executeGenerateDocumentTest("16", "", "Rule 21 of the Employment Tribunal Rules of Procedure 2013", true);
    }

    @Test
    public void generateDocument_Part_Scot_16_1() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "EMPLOYMENT TRIBUNALS", true);
    }

    @After
    public void tearDown() {

    }
}
