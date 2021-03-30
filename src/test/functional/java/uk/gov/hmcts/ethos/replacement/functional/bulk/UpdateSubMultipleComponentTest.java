package uk.gov.hmcts.ethos.replacement.functional.bulk;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.functional.ComponentTest;
import uk.gov.hmcts.ethos.replacement.functional.util.Constants;
import uk.gov.hmcts.ethos.replacement.functional.util.TestUtil;

import java.io.File;
import java.io.IOException;

@Category(ComponentTest.class)
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("ComponentTest"),
        @WithTag("FunctionalTest")
})
public class UpdateSubMultipleComponentTest {

    private TestUtil testUtil;

    @Before
    public void setUp() {
        testUtil = new TestUtil();
    }

    @Test
    public void update_sub_multiples_eng() throws IOException {
        testUtil.executeUpdateSubMultiples(false, Constants.TEST_DATA_ENG_UPDATE_SUB_MULTIPLE,
                24, "Manchester_Multiples_Dev");
    }

    @Test
    public void update_sub_multiples_scot() throws IOException {
        testUtil.executeUpdateSubMultiples(false, Constants.TEST_DATA_ENG_UPDATE_SUB_MULTIPLE,
                41, "Scotland_Multiples_Dev");
    }

    @Test
    public void update_sub_multiples_with_no_payload() throws IOException {
        testUtil.loadAuthToken();
        BulkRequest bulkRequest = new BulkRequest();
        Response response = testUtil.getBulkResponse(bulkRequest, Constants.UPDATE_BULK_CASE_URI, 500);
    }

    @Test
    @Ignore
    public void update_sub_multiples_with_no_token() throws IOException {
        testUtil.setAuthToken("authToken");
        String testData = FileUtils.readFileToString(new File(Constants.TEST_DATA_ENG_BULK1), "UTF-8");
        BulkRequest bulkRequest = testUtil.getBulkRequest(false, testData);
        Response response = testUtil.getBulkResponse(bulkRequest, Constants.UPDATE_BULK_CASE_URI, 500);
        testUtil.setAuthToken(null);
    }

    @After
    public void tearDown() {
    }
}
