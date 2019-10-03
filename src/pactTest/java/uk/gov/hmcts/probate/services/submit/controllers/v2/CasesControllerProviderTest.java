//package uk.gov.hmcts.probate.services.submit.controllers.v2;
//
//import au.com.dius.pact.provider.junit.Provider;
//import au.com.dius.pact.provider.junit.State;
//import au.com.dius.pact.provider.junit.loader.PactBroker;
//import au.com.dius.pact.provider.junit.target.HttpTarget;
//import au.com.dius.pact.provider.junit.target.Target;
//import au.com.dius.pact.provider.junit.target.TestTarget;
//import au.com.dius.pact.provider.spring.SpringRestPactRunner;
//import org.json.JSONException;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import uk.gov.hmcts.probate.services.submit.model.v2.exception.CaseNotFoundException;
//import uk.gov.hmcts.probate.services.submit.services.CasesService;
//import uk.gov.hmcts.reform.probate.model.cases.CaseType;
//
//import java.io.IOException;
//
//import static org.mockito.Mockito.when;
//
//@Provider("probate_submitservice_cases")
//@RunWith(SpringRestPactRunner.class)
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
//        "server.port=8123", "spring.application.name=PACT_TEST"
//})
//public class CasesControllerProviderTest extends ControllerProviderTest {
//
//    @TestTarget
//    @SuppressWarnings(value = "VisibilityModifier")
//    public final Target target = new HttpTarget("http", "localhost", 8123, "/");
//
//    @MockBean
//    private CasesService casesService;
//
//    @State({"provider returns casedata with success","provider returns casedata with success"})
//    public void toReturnCaseDetailsWithSuccess() throws IOException, JSONException {
//
//        when(casesService.getCase("jsnow@bbc.co.uk", CaseType.GRANT_OF_REPRESENTATION))
//                .thenReturn(getProbateCaseDetails("intestacyGrantOfRepresentation_full.json"));
//    }
//
//    @State({"provider returns casedata not found",
//            "provider returns casedata not found"})
//    public void toReturnCaseDetailsWithNotFound() {
//        when(casesService.getCase("jsnow@bbc.co.uk", CaseType.GRANT_OF_REPRESENTATION))
//                .thenThrow(CaseNotFoundException.class);
//    }
//
//}
