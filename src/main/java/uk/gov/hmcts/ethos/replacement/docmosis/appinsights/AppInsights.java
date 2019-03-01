//package uk.gov.hmcts.ethos.replacement.docmosis.appinsights;
//
//import com.microsoft.applicationinsights.TelemetryClient;
//import org.springframework.stereotype.Component;
//import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;
//
//import static java.util.Collections.singletonMap;
//
//@Component
//public class AppInsights extends AbstractAppInsights {
//
//    public static final String DOCUMENT_NAME = "document.name";
//
//    public AppInsights(TelemetryClient telemetryClient) {
//        super(telemetryClient);
//    }
//
//    public void trackEvent(AppInsightsEvent appInsightsEvent, String referenceType, String value) {
//        telemetry.trackEvent(appInsightsEvent.toString(), singletonMap(referenceType, value), null);
//    }
//}
