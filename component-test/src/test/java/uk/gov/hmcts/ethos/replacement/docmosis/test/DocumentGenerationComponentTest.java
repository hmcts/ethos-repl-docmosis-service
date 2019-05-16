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
public class DocumentGenerationComponentTest {


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

    @After
    public void tearDown() {

    }
}
