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
public class DocMosisPart14ComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void generateDocument_Part14_1() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "1", "REDUNDANCY and other PAYMENTS");
    }

    @Test
    public void generateDocument_Part14_2() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "2",
                "RESPONDENT COMPANY IN ADMINISTRATION");
    }

    @Test
    public void generateDocument_Part14_3() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "3",
                "You have not told us that you have obtained the consent of the Administrator or the permission");
    }

    @Test
    public void generateDocument_Part14_4() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "4",
                "The respondent company is in administration. Neither the consent "
                        + "of the Administrator nor the permission");
    }

    @Test
    public void generateDocument_Part14_5() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "5",
                "RESPONDENT COMPANY IN COMPULSORY LIQUIDATION");
    }

    @Test
    public void generateDocument_Part14_6() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "6",
                "You have not told us that you have obtained the permission of the court "
                        + "to allow your claim to proceed");
    }

    @Test
    public void generateDocument_Part14_7() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "7",
                "The permission of the court has not been obtained for these proceedings "
                        + "to be instituted or continued");
    }

    @Test
    public void generateDocument_Part14_8() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "8", "RESPONDENT COMPANY DISSOLVED");
    }

    @Test
    public void generateDocument_Part14_9() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "9",
                "You have not informed us that you have applied to have the Respondent company restored");
    }

    @Test
    public void generateDocument_Part14_10() throws Exception {
        testUtil.executeGenerateDocumentTest("14", "10",
                "The respondent company has been dissolved and the claimant");
    }

    @After
    public void tearDown() throws IOException {
        testUtil.deleteTempFile();
    }
}
