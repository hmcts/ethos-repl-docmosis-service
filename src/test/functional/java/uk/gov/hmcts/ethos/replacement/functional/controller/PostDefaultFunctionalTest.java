package uk.gov.hmcts.ethos.replacement.functional.controller;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.ethos.replacement.functional.FunctionalTest;

import java.io.IOException;

import static net.serenitybdd.rest.RestDefaults.useRelaxedHTTPSValidation;

@RunWith(SerenityRunner.class)
@TestPropertySource(locations = "classpath:config/application.properties")
public class PostDefaultFunctionalTest {
    private FuncHelper funcHelper;

    @Before
    public void setUp() {
        funcHelper = new FuncHelper();
        useRelaxedHTTPSValidation();
    }

    @Test
    @Category(FunctionalTest.class)
    public void claimantIndividualEng() throws IOException {
//        executeTest(Constants.TEST_DATA_POST_DEFAULT1, false);

    }
}
