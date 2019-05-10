package uk.gov.hmcts.ethos.replacement.docmosis.test;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.Constants;

import java.io.File;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;

@Category(ComponentTest.class)
public class DocumentGenerationComponentTest {

    @Before
    public void setUp() {

    }

    @Test
    public void generateDocument_Part1() throws IOException {
        given().contentType("application/json").
                auth().oauth2("Bearer auth").
                body(FileUtils.readFileToString(new File(Constants.TEST_DATA_CASE1), "UTF-8")).
                when().post(Constants.BASE_URL + Constants.URL_GEN_DOCUMENT).
                then().statusCode(200);
    }

    @After
    public void tearDown() {

    }
}
