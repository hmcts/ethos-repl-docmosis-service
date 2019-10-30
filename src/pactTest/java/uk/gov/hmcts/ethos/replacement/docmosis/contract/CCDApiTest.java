package uk.gov.hmcts.ethos.replacement.docmosis.contract;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@ExtendWith(PactConsumerTestExt.class)
@EnableFeignClients
@ExtendWith(SpringExtension.class)
@PactTestFor(providerName = "CCDService", port = "9999")
@SpringBootTest({
        // overriding provider address
        "CCDService.ribbon.listOfServers: localhost:8888", "ccd.data.store.api.url: http://localhost:9999"
})
@ContextConfiguration(classes = ConsumerApplication.class)
public class CCDApiTest {

    @Autowired
    private CcdClient ccdApi;

    @Pact(state = "Submit case in CCD", provider = "ccd-data-store-api", consumer = "ethos-repl-docmosis-service")
    RequestResponsePact submitCasePact(PactDslWithProvider builder) throws IOException, JSONException {
        String Uri = String.format("/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%s/events", "18", "EMPLOYMENT", "Manchester_Dev","1561736564850897");
        File file = getFile("caseDetailsTest1.json");
        String json = FileUtils.readFileToString(file, "UTF-8");
        File jsonResponse = getFile("caseDetailsTest1Response.json");
        String jsonRes = FileUtils.readFileToString(jsonResponse, "UTF-8");
        JSONObject jSONObject = new JSONObject(jsonRes);
        jSONObject.put("state","stateValue");

        ObjectMapper mapper = new ObjectMapper();
        CCDRequest ccdObject = mapper.readValue(json, CCDRequest.class);

        return builder
                .given("provider submit a new Case")
                .uponReceiving("a request to submit Case")
                .path(Uri)
                .method("POST")
                .headers("Authorization","authToken","Serviceauthorization","authToken")
                .body(json)
                .willRespondWith()
                .status(201)
                .matchHeader("Content-Type", "application/json")
                .body(new PactDslJsonBody()
                        .includesStr("state", "stateValue"))
                .toPact();
        // @formatter:on
    }

    @Test
    @PactTestFor(pactMethod = "submitCasePact")
    void verifyCreateCase() throws IOException {
        File file = getFile("caseDetailsTest1.json");
        String json = FileUtils.readFileToString(file, "UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        CCDRequest ccdObject = mapper.readValue(json, CCDRequest.class);

        UserDetails userDetails = new UserDetails("18","email","forename","surname",new ArrayList<>());
        Mockito.when(mockUserService.getUserDetails("authToken")).thenReturn(userDetails);

        SubmitEvent submitEvent = ccdApi.submitEventForCase("authToken",new CaseData(),"Manchester_Dev","EMPLOYMENT",ccdObject,"1561736564850897");
        Assert.assertEquals("stateValue", submitEvent.getState());
    }

    @Pact(state = "Get the case in CCD", provider = "ccd-data-store-api", consumer = "ethos-repl-docmosis-service1")
    RequestResponsePact getCasePact(PactDslWithProvider builder) throws IOException {
        String Uri = String.format("/caseworkers/%s/jurisdictions/%s/case-types/%s/event-triggers/%s/token", "userId", "JID", "CASETYPEID","initiateCase");

        String json = FileUtils.readFileToString(new File("src/test/resources/caseDetailsTest1.json"), "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        CCDRequest ccdObject = mapper.readValue(json, CCDRequest.class);

        return builder
                .given("provider to get the created Case")
                .uponReceiving("a request to get the Case")
                .path(Uri)
                .method("GET")
                .headers("Authorization","authToken","Serviceauthorization","authToken")
                .matchQuery("ignore-warning", "true")
                .willRespondWith()
                .status(200)
                .matchHeader("Content-Type", "application/json")
                .body(new PactDslJsonBody()
                        .includesStr("event_id", "value")
                        .includesStr("token", "tokenValue"))

                .toPact();
        // @formatter:on
    }

    @MockBean
    public UserService mockUserService;

    @Test
    @PactTestFor(pactMethod = "getCasePact")
    void verifyCreateCase2() throws IOException {

        String json = FileUtils.readFileToString(new File("src/test/resources/caseDetailsTest1.json"), "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        CCDRequest ccdObject = mapper.readValue(json, CCDRequest.class);
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setJurisdiction("JID");
        caseDetails.setCaseTypeId("CASETYPEID");
        UserDetails userDetails = new UserDetails("userId","email","forename","surname",new ArrayList<>());
        Mockito.when(mockUserService.getUserDetails("authToken")).thenReturn(userDetails);
        CCDRequest response = ccdApi.startCaseCreation("authToken",caseDetails);
        Assert.assertEquals("value", response.getEventId());
        Assert.assertEquals("tokenValue", response.getToken());
    }

    private File getFile(String fileName) throws FileNotFoundException {
        return ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
    }

}
