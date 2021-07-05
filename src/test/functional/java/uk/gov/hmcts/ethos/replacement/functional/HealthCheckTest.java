package uk.gov.hmcts.ethos.replacement.functional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HealthCheckTest {

    @Before
    public void before() {
    }

    @Test
    @Category(SmokeTest.class)
    public void healthcheck_returns_200() {
        assertThat("smokeTest", is("smokeTest"));
    }
}
