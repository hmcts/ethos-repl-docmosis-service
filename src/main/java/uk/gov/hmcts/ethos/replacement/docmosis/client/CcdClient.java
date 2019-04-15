package uk.gov.hmcts.ethos.replacement.docmosis.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class CcdClient {

    private RestTemplate restTemplate;
    private UserService userService;
    private CcdClientConfig ccdClientConfig;
    private CaseDataBuilder caseDataBuilder;

    @Autowired
    public CcdClient(RestTemplate restTemplate, UserService userService,
                     CaseDataBuilder caseDataBuilder, CcdClientConfig ccdClientConfig) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.caseDataBuilder = caseDataBuilder;
        this.ccdClientConfig = ccdClientConfig;
    }

    public CCDRequest startCaseCreation(String authToken, CaseDetails caseDetails) throws IOException {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartCaseCreationUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId());
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public SubmitEvent submitCaseCreation(String authToken, CaseDetails caseDetails, CCDRequest req) throws IOException {
        HttpEntity<CaseDataContent> request =
                new HttpEntity<>(caseDataBuilder.buildCaseDataContent(caseDetails, req), ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildSubmitCaseCreationUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId());
        return restTemplate.exchange(uri, HttpMethod.POST, request, SubmitEvent.class).getBody();
    }

    public SubmitEvent retrieveCase(String authToken, CaseDetails caseDetails, String cid) throws IOException {
        HttpEntity<CCDRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildRetrieveCaseUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId(), cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, SubmitEvent.class).getBody();
    }

    public List<SubmitEvent> retrieveCases(String authToken, CaseDetails caseDetails) throws IOException {
        HttpEntity<CCDRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildRetrieveCasesUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId());
        return restTemplate.exchange(uri, HttpMethod.GET, request, new ParameterizedTypeReference<List<SubmitEvent>>(){}).getBody();
    }

    public CCDRequest startEventForCase(String authToken, CaseDetails caseDetails, String cid) throws IOException {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartEventForCaseUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId(), cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public SubmitEvent submitEventForCase(String authToken, CaseDetails caseDetails, CCDRequest req, String cid) throws IOException {
        HttpEntity<CaseDataContent> request =
                new HttpEntity<>(caseDataBuilder.buildCaseDataContent(caseDetails, req), ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildSubmitEventForCaseUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId(), cid);
        return restTemplate.exchange(uri, HttpMethod.POST, request, SubmitEvent.class).getBody();
    }

}
