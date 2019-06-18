package uk.gov.hmcts.ethos.replacement.docmosis.test.docgen;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ethos.replacement.docmosis.test.ComponentTest;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.TestUtil;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class DocMosisPart15ComponentTest {


    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }


    @Test
    @WithTag("SmokeTest")
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
