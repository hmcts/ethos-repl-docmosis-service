package uk.gov.hmcts.ethos.replacement.functional.docgen;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.IOException;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class DocMosisPart9ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part9_1() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "1A", "HEARING IN PRIVATE");
    }

    @Test
    public void generateDocument_Part9_1B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "1B", "HEARING IN PRIVATE");
    }

    @Test
    public void generateDocument_Part9_2A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "2A",
                "Pursuant  to rules 50\\(1\\) and \\(3\\)\\(b\\) of the Employment "
                        + "Tribunals Rules of Procedure 2013, it being in the interests of justice "
                        + "to do so, it is ORDERED");
    }

    @Test
    public void generateDocument_Part9_2B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "2B", "other provision to "
                + "be specified by the judge");
    }

    @Test
    public void generateDocument_Part9_3A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "3A",
                "Pursuant to rules 50\\(1\\) and \\(3\\)\\(c\\) of the Employment "
                        + "Tribunals Rules of Procedure 2013, it being in the interests of justice "
                        + "to do so, it is ORDERED");
    }

    @Test
    public void generateDocument_Part9_3B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "3B",
                "Pursuant to rules 50\\(1\\) and \\(3\\)\\(c\\) of the Employment "
                        + "Tribunals Rules of Procedure 2013 and  \\[Art 8 of the European "
                        + "Convention on Human Rights\\]");
    }

    @Test
    public void generateDocument_Part9_4() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "4", "RESTRICTED REPORTING ORDER");
    }

    @Test
    public void generateDocument_Part9_5() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "5", "RESTRICTED REPORTING ORDER");
    }

    @Test
    public void generateDocument_Part9_6A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "6A",
                "rules 50\\(1\\) and 29 of the Employment Tribunals Rules of Procedure 2013, "
                        + "it being in the interests of justice to do so");
    }

    @Test
    public void generateDocument_Part9_6B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "6B",
                "Employment Tribunals Act 1996, rules 50\\(1\\) and 29 of the Employment "
                        + "Tribunals Rules of Procedure 2013 and");
    }

    @Test
    public void generateDocument_Part9_7A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "7A", "RESTRICTED REPORTING ORDER");
    }

    @Test
    public void generateDocument_Part9_7B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "7B",
                "Pursuant to section 12 of the Employment Tribunals Act 1996 and rules 50\\(1\\) and "
                        + "29 of the Employment Tribunals Rules of Procedure 2013 and");
    }

    @Test
    public void generateDocument_Part9_8A() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "8A",
                "Pursuant to rules 50\\(1\\) and 29 of the Employment Tribunals Rules of Procedure 2013, "
                        + "it being in the interest of justice to do so");
    }

    @Test
    public void generateDocument_Part9_8B() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "8B",
                "Pursuant to rules 50\\(1\\) and 29 of the Employment Tribunals Rules of Procedure 2013 and");
    }

    @Test
    public void generateDocument_Part9_9() throws Exception {
        testUtil.executeGenerateDocumentTest("9", "9", "NOTICE OF A RESTRICTED REPORTING ORDER");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
