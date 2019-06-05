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
public class DocMosisScotPart7ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }

    @Test
    public void generateDocument_Part_Scot_58() throws Exception {
        testUtil.executeGenerateDocumentTest("58", "", "STRIKE OUT WARNING TO CLAIMANT", true);
    }

    @Test
    public void generateDocument_Part_Scot_59() throws Exception {
        testUtil.executeGenerateDocumentTest("59", "", "STRIKE OUT WARNING TO RESPONDENT", true);
    }

    @Test
    public void generateDocument_Part_Scot_60() throws Exception {
        testUtil.executeGenerateDocumentTest("60", "", "is struck out under rule 37 of the Rules contained in Schedule 1", true);
    }

    @Test
    public void generateDocument_Part_Scot_61() throws Exception {
        testUtil.executeGenerateDocumentTest("61", "", "RESULT OF STRIKE OUT APPLICATION", true);
    }

    @Test
    public void generateDocument_Part_Scot_62() throws Exception {
        testUtil.executeGenerateDocumentTest("62", "", "ORDER TO PAY A DEPOSIT", true);
    }

    @Test
    public void generateDocument_Part_Scot_63() throws Exception {
        testUtil.executeGenerateDocumentTest("", "", "The allegation/argument", true);
    }

    @After
    public void tearDown() {

    }
}
