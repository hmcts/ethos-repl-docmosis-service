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
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import uk.gov.hmcts.probate.services.submit.services.DraftService;
//import uk.gov.hmcts.reform.probate.model.cases.ProbateCaseDetails;
//
//import java.io.IOException;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@Provider("probate_submitservice_drafts")
//@RunWith(SpringRestPactRunner.class)
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
//        "server.port=8124", "spring.application.name=PACT_TEST"
//})
//public class DraftsControllerProviderTest extends ControllerProviderTest {
//
//    @TestTarget
//    @SuppressWarnings(value = "VisibilityModifier")
//    public final Target target = new HttpTarget("http", "localhost", 8124, "/");
//
//    @MockBean
//    private DraftService draftService;
//
//    @State({"provider POSTS draft casedata with success",
//            "provider POSTS draft casedata with success"})
//    public void toPostDraftCaseDetailsWithSuccess() throws IOException, JSONException {
//
//        when(draftService.saveDraft(anyString(),any(ProbateCaseDetails.class)))
//                .thenReturn(getProbateCaseDetails("intestacyGrantOfRepresentation_full.json"));
//    }
//
//    @State({"provider POSTS partial draft casedata with success",
//            "provider POSTS partial draft casedata with success"})
//    public void toPostPartialDraftCaseDetailsWithSuccess() throws IOException, JSONException {
//
//        when(draftService.saveDraft(anyString(),any(ProbateCaseDetails.class)))
//                .thenReturn(getProbateCaseDetails("intestacyGrantOfRepresentation_partial_draft.json"));
//    }
//
//}
