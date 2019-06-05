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
public class DocMosisPart15ComponentTest {


    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp() {
    }


    @Test
    public void generateDocument_Part15_1() throws Exception {
        testUtil.executeGenerateDocumentTest("15", "1", "Thank you for your interest in judicial mediation");
    }

    @Test
    public void generateDocument_Part15_2() throws Exception {
        testUtil.executeGenerateDocumentTest("15", "2", "NOTICE OF PRELIMINARY HEARING BY TELEPHONE");
    }

    @Test
    public void generateDocument_Part15_3() throws Exception {
        testUtil.executeGenerateDocumentTest("15", "", "NOTICE OF JUDICIAL MEDIATION PRELIMINARY HEARING");
    }


    @After
    public void tearDown() {

    }
}
