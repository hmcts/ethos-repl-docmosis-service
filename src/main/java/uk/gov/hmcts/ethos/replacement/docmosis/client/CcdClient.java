package uk.gov.hmcts.ethos.replacement.docmosis.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class CcdClient {

    private RestTemplate restTemplate;
    private UserService userService;
    private final ObjectMapper objectMapper;
    private CcdClientConfig ccdClientConfig;
    private static final String EVENT_SUMMARY = "case created automatically";
    private static final Boolean IGNORE_WARNING = Boolean.FALSE;

    @Autowired
    public CcdClient(RestTemplate restTemplate, UserService userService,
                     ObjectMapper objectMapper, CcdClientConfig ccdClientConfig) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.ccdClientConfig = ccdClientConfig;
    }

    public CCDRequest startCaseCreation(String authToken, CaseDetails caseDetails) {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartCaseCreationUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId());
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public SubmitEvent submitCaseCreation(String authToken, CaseDetails caseDetails, CCDRequest req) {
        HttpEntity<CaseDataContent> request =
                new HttpEntity<>(buildCaseDataContent(caseDetails, req), ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildSubmitCaseCreationUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId());
        return restTemplate.exchange(uri, HttpMethod.POST, request, SubmitEvent.class).getBody();
    }

    public SubmitEvent retrieveCase(String authToken, CaseDetails caseDetails, String cid) {
        HttpEntity<CCDRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildRetrieveCaseUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId(), cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, SubmitEvent.class).getBody();
    }

    private CaseDataContent buildCaseDataContent(CaseDetails caseDetails, CCDRequest req) {
        Map<String, JsonNode> data = objectMapper.convertValue(caseDetails.getCaseData(), new TypeReference<Map<String, JsonNode>>(){});
        return CaseDataContent.builder()
                .event(Event.builder().eventId(req.getEventId()).summary(EVENT_SUMMARY).build())
                .data(data)
                .token(req.getToken())
                .ignoreWarning(IGNORE_WARNING)
                .build();
    }
}
