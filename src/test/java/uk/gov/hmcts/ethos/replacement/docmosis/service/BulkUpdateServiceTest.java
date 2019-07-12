package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.SubmitBulkEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.CaseType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.SearchType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class BulkUpdateServiceTest {

    @InjectMocks
    private BulkUpdateService bulkUpdateService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private BulkSearchService bulkSearchService;

    private CCDRequest ccdRequest;
    private BulkRequest bulkRequest;
    private SubmitEvent submitEvent;
    private SearchTypeItem searchTypeItem;
    private BulkDetails bulkDetails;
    private SubmitBulkEvent submitBulkEvent;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        bulkRequest = new BulkRequest();
        bulkDetails = new BulkDetails();
        BulkData bulkData = new BulkData();
        bulkData.setMultipleReference("1111");
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseData(bulkData);
        bulkDetails.setCaseTypeId(MANCHESTER_BULK_CASE_TYPE_ID);
        bulkRequest.setCaseDetails(bulkDetails);

        CaseData caseData = new CaseData();
        caseData.setMultipleReference("2222");
        caseData.setCaseType("Multiple");
        caseData.setEthosCaseReference("111");
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("JuanPedro");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Mike");
        caseData.setRespondentSumType(respondentSumType);
        RepresentedTypeR representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfRepresentative("Juan");
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setValue(representedTypeR);
        caseData.setRepCollection(new ArrayList<>(Collections.singletonList(representedTypeRItem)));
        submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1111);
        submitEvent.setCaseData(caseData);
        submitEvent.setState("1_Submitted");
        searchTypeItem = new SearchTypeItem();
        searchTypeItem.setId("11111");

        submitBulkEvent = new SubmitBulkEvent();
        submitBulkEvent.setCaseId(1111);
        submitBulkEvent.setCaseData(bulkData);

        bulkSearchService = new BulkSearchService(ccdClient);
        bulkUpdateService = new BulkUpdateService(ccdClient, bulkSearchService);
    }

    @Test(expected = Exception.class)
    public void caseUpdateMultipleReferenceRequestException() throws IOException {
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenThrow(feignError());
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkRequest.getCaseDetails(), submitEvent, "authToken", "11111", "Multiple");
    }

    @Test
    public void caseUpdateMultipleReferenceRequest() throws IOException {
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkRequest.getCaseDetails(), submitEvent, "authToken", "11111", "Multiple");
    }

    @Test
    public void caseUpdateMultipleReferenceRequestPending() throws IOException {
        when(ccdClient.startEventForCaseBulkSingle(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        submitEvent.setState(PENDING_STATE);
        bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkRequest.getCaseDetails(), submitEvent, "authToken", "11111", "Multiple");
    }

    @Test(expected = Exception.class)
    public void caseUpdateFieldsRequestException() throws IOException {
        when(ccdClient.retrieveCase("authToken", MANCHESTER_CASE_TYPE_ID, bulkDetails.getJurisdiction(), searchTypeItem.getId())).thenThrow(feignError());
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        bulkUpdateService.caseUpdateFieldsRequest(bulkRequest.getCaseDetails(), searchTypeItem, "authToken", submitBulkEvent);
    }

    @Test
    public void caseUpdateFieldsRequest() throws IOException {
        when(ccdClient.retrieveCase("authToken", MANCHESTER_USERS_CASE_TYPE_ID, bulkDetails.getJurisdiction(), searchTypeItem.getId())).thenReturn(submitEvent);
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        bulkUpdateService.caseUpdateFieldsRequest(bulkRequest.getCaseDetails(), searchTypeItem, "authToken", submitBulkEvent);
    }

    @Test
    public void caseUpdateFieldsWithNewValuesRequest() throws IOException {
        when(ccdClient.retrieveCase("authToken", MANCHESTER_CASE_TYPE_ID, bulkDetails.getJurisdiction(), searchTypeItem.getId())).thenReturn(submitEvent);
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        bulkUpdateService.caseUpdateFieldsRequest(getBulkDetailsWithValues(), searchTypeItem, "authToken", submitBulkEvent);
    }

    @Test
    public void bulkUpdateLogic() throws IOException {
        SubmitBulkEvent submitBulkEvent = new SubmitBulkEvent();
        BulkData bulkData = new BulkData();
        bulkData.setMultipleReference("1111");
        submitBulkEvent.setCaseData(bulkData);
        List<SubmitBulkEvent> submitBulkEventList = new ArrayList<>(Collections.singletonList(submitBulkEvent));
        when(ccdClient.retrieveBulkCases("authToken", MANCHESTER_BULK_CASE_TYPE_ID, bulkDetails.getJurisdiction())).thenReturn(submitBulkEventList);
        when(ccdClient.retrieveCase("authToken", MANCHESTER_CASE_TYPE_ID, bulkDetails.getJurisdiction(), searchTypeItem.getId())).thenReturn(submitEvent);
        assert(bulkUpdateService.bulkUpdateLogic(getBulkDetailsCompleteWithValues(getBulkDetailsWithValues()),
                "authToken").getBulkDetails() != null);
        submitEvent.getCaseData().setRepCollection(null);
        submitEvent.getCaseData().setClaimantIndType(null);
        submitEvent.getCaseData().setRespondentSumType(null);
        when(ccdClient.retrieveCase("authToken", MANCHESTER_CASE_TYPE_ID, bulkDetails.getJurisdiction(), searchTypeItem.getId())).thenReturn(submitEvent);
        assert(bulkUpdateService.bulkUpdateLogic(getBulkDetailsCompleteWithValues(getBulkDetailsWithValues()),
                "authToken").getBulkDetails() != null);
    }

    @Test
    public void bulkUpdateLogicWithErrors() throws IOException {
        SubmitBulkEvent submitBulkEvent = new SubmitBulkEvent();
        BulkData bulkData = new BulkData();
        bulkData.setMultipleReference("1111");
        submitBulkEvent.setCaseData(bulkData);
        List<SubmitBulkEvent> submitBulkEventList = new ArrayList<>(Collections.singletonList(submitBulkEvent));
        when(ccdClient.retrieveBulkCases("authToken", MANCHESTER_BULK_CASE_TYPE_ID, bulkDetails.getJurisdiction())).thenReturn(submitBulkEventList);
        when(ccdClient.retrieveCase("authToken", MANCHESTER_CASE_TYPE_ID, bulkDetails.getJurisdiction(), searchTypeItem.getId())).thenReturn(submitEvent);
        assert(!bulkUpdateService.bulkUpdateLogic(getBulkDetailsWithValues(), "authToken").getErrors().isEmpty());
    }

    private BulkDetails getBulkDetailsWithValues() {
        BulkDetails bulkDetails = new BulkDetails();
        BulkData bulkData = new BulkData();
        bulkData.setFileLocationV2("Glasgow");
        bulkData.setRespondentSurnameV2("Respondent");
        bulkData.setClaimantSurnameV2("Claimant");
        bulkData.setMultipleReferenceV2("1111");
        bulkData.setClerkResponsibleV2("Juan");
        bulkData.setPositionTypeV2("Awaiting");
        bulkData.setClaimantRepV2("ClaimantRep");
        bulkData.setRespondentRepV2("RespondentRep");
        bulkDetails.setCaseData(bulkData);
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId(MANCHESTER_BULK_CASE_TYPE_ID);
        return bulkDetails;
    }

    private BulkDetails getBulkDetailsCompleteWithValues(BulkDetails bulkDetails) {
        BulkData bulkData = bulkDetails.getCaseData();
        SearchTypeItem searchTypeItem1 = new SearchTypeItem();
        SearchType searchType = new SearchType();
        searchType.setEthosCaseReferenceS("22222");
        searchTypeItem1.setId("11111");
        searchTypeItem1.setValue(searchType);
        bulkData.setSearchCollection(new ArrayList<>(Collections.singletonList(searchTypeItem1)));
        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference("2221");
        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setValue(caseType);
        bulkData.setCaseIdCollection(new ArrayList<>(Collections.singletonList(caseIdTypeItem)));
        bulkDetails.setCaseData(bulkData);
        return bulkDetails;
    }
}