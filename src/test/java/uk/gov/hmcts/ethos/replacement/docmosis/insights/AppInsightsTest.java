package uk.gov.hmcts.ethos.replacement.docmosis.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsights;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppInsightsTest {

    @Test
    public void newInstance() {
        TelemetryContext telemetryContext = new TelemetryContext();
        telemetryContext.setInstrumentationKey("key");

        TelemetryClient telemetryClient = mock(TelemetryClient.class);
        when(telemetryClient.getContext()).thenReturn(telemetryContext);

        assertThat(new AppInsights(telemetryClient), is(notNullValue()));
    }

}