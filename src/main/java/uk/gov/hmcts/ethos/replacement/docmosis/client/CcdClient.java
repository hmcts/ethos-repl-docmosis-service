package uk.gov.hmcts.ethos.replacement.docmosis.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ESHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkCaseSearchResult;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.SubmitBulkEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.ALL_VENUES;

@Slf4j
@Component
public class CcdClient {

    private RestTemplate restTemplate;
    private UserService userService;
    private CcdClientConfig ccdClientConfig;
    private CaseDataBuilder caseDataBuilder;

    static final String CREATION_EVENT_SUMMARY = "Case created automatically";
    private static final String UPDATE_EVENT_SUMMARY = "Case updated by bulk";
    static final String UPDATE_BULK_EVENT_SUMMARY = "Bulk case updated by bulk";

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
                new HttpEntity<>(caseDataBuilder.buildCaseDataContent(caseDetails.getCaseData(), req, CREATION_EVENT_SUMMARY), ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildSubmitCaseCreationUrl(userService.getUserDetails(authToken).getId(), caseDetails.getJurisdiction(),
                caseDetails.getCaseTypeId());
        return restTemplate.exchange(uri, HttpMethod.POST, request, SubmitEvent.class).getBody();
    }

    public SubmitEvent retrieveCase(String authToken, String caseTypeId, String jurisdiction, String cid) throws IOException {
        HttpEntity<CCDRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildRetrieveCaseUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, SubmitEvent.class).getBody();
    }

    private PaginatedSearchMetadata searchMetadata(String authToken, String caseTypeId, String jurisdiction) throws IOException {
        HttpEntity<CCDRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildPaginationMetadataCaseUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId);
        return restTemplate.exchange(uri, HttpMethod.GET, request, PaginatedSearchMetadata.class).getBody();
    }

    private String getURI(String authToken, String caseTypeId, String jurisdiction, String page) {
        return ccdClientConfig.buildRetrieveCasesUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, page);
    }

    private int getTotalPagesCount(String authToken, String caseTypeId, String jurisdiction) throws IOException {
        PaginatedSearchMetadata paginatedSearchMetadata = searchMetadata(authToken, caseTypeId, jurisdiction);
        return paginatedSearchMetadata.getTotalPagesCount();
    }

    public List<SubmitEvent> retrieveCases(String authToken, String caseTypeId, String jurisdiction) throws IOException {
        HttpEntity<CCDRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        List<SubmitEvent> submitEvents = new ArrayList<>();
        for (int page = 1; page <= getTotalPagesCount(authToken, caseTypeId, jurisdiction); page++) {
            List<SubmitEvent> submitEventAux = restTemplate.exchange(ccdClientConfig.buildRetrieveCasesUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, String.valueOf(page)), HttpMethod.GET, request, new ParameterizedTypeReference<List<SubmitEvent>>(){}).getBody();
            if (submitEventAux != null) {
                submitEvents.addAll(submitEventAux);
            }
        }
        return submitEvents;
    }

    private String getListingQuery(String from, String to, String venue, String mapping) {
        if (ALL_VENUES.equals(venue)) {
            log.info("QUERY DATE: " + ESHelper.getListingRangeDateSearchQuery(from, to));
            return ESHelper.getListingRangeDateSearchQuery(from, to);
        } else {
            log.info("QUERY DATE + VENUE: " + ESHelper.getListingVenueAndRangeDateSearchQuery(from, to, venue, mapping));
            return ESHelper.getListingVenueAndRangeDateSearchQuery(from, to, venue, mapping);
        }
    }

    public List<SubmitEvent> retrieveCasesVenueAndDateElasticSearch(String authToken, String caseTypeId, String dateToSearchFrom, String dateToSearchTo,
                                                                 String venueToSearch, String venueToSearchMapping) throws IOException {
        if (dateToSearchTo.equals(dateToSearchFrom)) {
            return buildAndGetElasticSearchRequest(authToken, caseTypeId,
                    getListingQuery(dateToSearchFrom, dateToSearchFrom, venueToSearch, venueToSearchMapping));
        } else {
            return buildAndGetElasticSearchRequest(authToken, caseTypeId,
                    getListingQuery(dateToSearchFrom, dateToSearchTo, venueToSearch, venueToSearchMapping));
        }
    }

    private List<SubmitEvent> buildAndGetElasticSearchRequest(String authToken, String caseTypeId, String query) throws IOException {
        List<SubmitEvent> submitEvents = new ArrayList<>();
        HttpEntity<String> request = new HttpEntity<>(query, ccdClientConfig.buildHeaders(authToken));
        String url = ccdClientConfig.buildRetrieveCasesUrlElasticSearch(caseTypeId);
        CaseSearchResult caseSearchResult = restTemplate.exchange(url, HttpMethod.POST, request, CaseSearchResult.class).getBody();
        if (caseSearchResult != null && caseSearchResult.getCases() != null) {
            submitEvents.addAll(caseSearchResult.getCases());
        }
        return submitEvents;
    }

    public List<SubmitEvent> retrieveCasesElasticSearch(String authToken, String caseTypeId, List<String> caseIds) throws IOException {
        log.info("QUERY: " + ESHelper.getSearchQuery(caseIds));
        return buildAndGetElasticSearchRequest(authToken, caseTypeId, ESHelper.getSearchQuery(caseIds));
    }

    public List<SubmitBulkEvent> retrieveBulkCases(String authToken, String caseTypeId, String jurisdiction) throws IOException {
        HttpEntity<BulkRequest> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        List<SubmitBulkEvent> submitBulkEvents = new ArrayList<>();
        int totalNumberPages = getTotalPagesCount(authToken, caseTypeId, jurisdiction);
        for (int page = 1; page <= totalNumberPages; page++) {
            List<SubmitBulkEvent> submitBulkEventAux = restTemplate.exchange(getURI(authToken, caseTypeId, jurisdiction, String.valueOf(page)), HttpMethod.GET, request,
                    new ParameterizedTypeReference<List<SubmitBulkEvent>>(){}).getBody();
            if (submitBulkEventAux != null) {
                submitBulkEvents.addAll(submitBulkEventAux);
            }
        }
        return submitBulkEvents;
    }

    public List<SubmitBulkEvent> retrieveBulkCasesElasticSearch(String authToken, String caseTypeId, String multipleReference) throws IOException {
        List<SubmitBulkEvent> submitBulkEvents = new ArrayList<>();
        log.info("QUERY: " + ESHelper.getBulkSearchQuery(multipleReference));
        HttpEntity<String> request =
                new HttpEntity<>(ESHelper.getBulkSearchQuery(multipleReference), ccdClientConfig.buildHeaders(authToken));
        //log.info("REQUEST: " + request);
        String url = ccdClientConfig.buildRetrieveCasesUrlElasticSearch(caseTypeId);
        BulkCaseSearchResult bulkCaseSearchResult = restTemplate.exchange(url, HttpMethod.POST, request, BulkCaseSearchResult.class).getBody();
        if (bulkCaseSearchResult != null && bulkCaseSearchResult.getCases() != null) {
            submitBulkEvents.addAll(bulkCaseSearchResult.getCases());
        }
        return submitBulkEvents;
    }

    public CCDRequest startEventForCase(String authToken, String caseTypeId, String jurisdiction, String cid) throws IOException {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartEventForCaseUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public CCDRequest startEventForCaseBulkSingle(String authToken, String caseTypeId, String jurisdiction, String cid) throws IOException {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartEventForCaseUrlBulkSingle(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public CCDRequest startEventForCasePreAcceptBulkSingle(String authToken, String caseTypeId, String jurisdiction, String cid) throws IOException {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartEventForCaseUrlPreAcceptBulkSingle(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public CCDRequest startBulkEventForCase(String authToken, String caseTypeId, String jurisdiction, String cid) throws IOException {
        HttpEntity<String> request =
                new HttpEntity<>(ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildStartEventForBulkCaseUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.GET, request, CCDRequest.class).getBody();
    }

    public SubmitEvent submitEventForCase(String authToken, CaseData caseData, String caseTypeId, String jurisdiction, CCDRequest req, String cid)
            throws IOException {
        HttpEntity<CaseDataContent> request =
                new HttpEntity<>(caseDataBuilder.buildCaseDataContent(caseData, req, UPDATE_EVENT_SUMMARY), ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildSubmitEventForCaseUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.POST, request, SubmitEvent.class).getBody();
    }

    public SubmitBulkEvent submitBulkEventForCase(String authToken, BulkData bulkData, String caseTypeId, String jurisdiction, CCDRequest req, String cid)
            throws IOException {
        HttpEntity<CaseDataContent> request =
                new HttpEntity<>(caseDataBuilder.buildBulkDataContent(bulkData, req, UPDATE_BULK_EVENT_SUMMARY), ccdClientConfig.buildHeaders(authToken));
        String uri = ccdClientConfig.buildSubmitEventForCaseUrl(userService.getUserDetails(authToken).getId(), jurisdiction,
                caseTypeId, cid);
        return restTemplate.exchange(uri, HttpMethod.POST, request, SubmitBulkEvent.class).getBody();
    }

}
