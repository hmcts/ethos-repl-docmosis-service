package uk.gov.hmcts.ethos.replacement.functional.defaults;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.ResponseUtil;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.IOException;
import java.net.URL;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class PreDefaultComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    @WithTag("SmokeTest")
    public void claimant_type_individual_with_england_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", INDIVIDUAL_TYPE_CLAIMANT, false,
                Constants.TEST_DATA_PRE_DEFAULT1);
    }

    @Test
    public void claimant_type_company_with_england_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", INDIVIDUAL_TYPE_CLAIMANT, false,
                Constants.TEST_DATA_PRE_DEFAULT2);
    }

    @Test
    @WithTag("SmokeTest")
    public void claimant_type_individual_with_scotland_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", INDIVIDUAL_TYPE_CLAIMANT, true,
                Constants.TEST_DATA_SCOT_PRE_DEFAULT1);
    }

    @Test
    public void claimant_type_company_with_scotland_template() throws IOException {
        testUtil.executePreDefaultValuesTest("claimant_TypeOfClaimant", INDIVIDUAL_TYPE_CLAIMANT, true,
                Constants.TEST_DATA_SCOT_PRE_DEFAULT2);
    }

    @Ignore
    @Test
    public void invoke_pre_default_endpoint_without_auth_token() throws IOException {
        CCDRequest ccdRequest = testUtil.getCcdRequest("1", "1", false, Constants.TEST_DATA_PRE_DEFAULT1);

        try {
            testUtil.setAuthToken("Bearer authToken");
            Response response = testUtil.getResponse(ccdRequest, Constants.PRE_DEFAULT_URI, 401);
        } finally {
            testUtil.setAuthToken(null);
        }
    }

    @Ignore
    @Test
    public void invoke_pre_default_endpoint_without_payload() throws IOException {
        testUtil.loadAuthToken();

        CCDRequest ccdRequest = new CCDRequest();

        Response response = testUtil.getResponse(ccdRequest, Constants.PRE_DEFAULT_URI, 400);
    }

    @Test
    public void invoke_pre_default_as_get() throws IOException {
        String docmosisUrl = ResponseUtil.getProperty(testUtil.getEnvironment().toLowerCase() + ".docmosis.api.url");
        SerenityRest.given().get(new URL(docmosisUrl + Constants.PRE_DEFAULT_URI)).then().statusCode(405);
    }

    @After
    public void tearDown() {
    }
}
