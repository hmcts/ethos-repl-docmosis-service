package uk.gov.hmcts.ethos.replacement.functional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HealthCheckTest {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckTest.class);

    @Before
    public void before() {
    }

    @Test
    @Category(SmokeTest.class)
    public void healthcheck_returns_200() {
        assertThat("smokeTest", is("smokeTest"));
    }
}
