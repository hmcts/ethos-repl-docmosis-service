package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseUpdateForCaseWorkerServiceTest {

    @InjectMocks
    private CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService;
    @Mock
    private DefaultValuesReaderService defaultValuesReaderService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest manchesterCcdRequest;
    private CCDRequest glasgowCcdRequest;
    private SubmitEvent submitEvent;
    private DefaultValues manchesterDefaultValues;
    private DefaultValues glasgowDefaultValues;

    @Before
    public void setUp() {
        submitEvent = new SubmitEvent();

        manchesterCcdRequest = new CCDRequest();
        CaseDetails manchesterCaseDetails = new CaseDetails();
        manchesterCaseDetails.setCaseData(new CaseData());
        manchesterCaseDetails.setCaseId("123456");
        manchesterCaseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        manchesterCaseDetails.setJurisdiction("TRIBUNALS");
        manchesterCcdRequest.setCaseDetails(manchesterCaseDetails);

        glasgowCcdRequest = new CCDRequest();
        CaseDetails glasgowCaseDetails = new CaseDetails();
        glasgowCaseDetails.setCaseData(new CaseData());
        glasgowCaseDetails.setCaseId("123456");
        glasgowCaseDetails.setCaseTypeId(GLASGOW_CASE_TYPE_ID);
        glasgowCaseDetails.setJurisdiction("TRIBUNALS");
        glasgowCcdRequest.setCaseDetails(glasgowCaseDetails);

        caseUpdateForCaseWorkerService = new CaseUpdateForCaseWorkerService(ccdClient, defaultValuesReaderService);
        manchesterDefaultValues = DefaultValues.builder()
                .positionType("Awaiting ET3")
                .claimantTypeOfClaimant("Individual")
                .tribunalCorrespondenceAddressLine1("Manchester Employment Tribunal,")
                .tribunalCorrespondenceAddressLine2("Alexandra House,")
                .tribunalCorrespondenceAddressLine3("14-22 The Parsonage,")
                .tribunalCorrespondenceTown("Manchester,")
                .tribunalCorrespondencePostCode("M3 2JA")
                .tribunalCorrespondenceTelephone("0161 833 6100")
                .tribunalCorrespondenceFax("7577126570")
                .tribunalCorrespondenceDX("123456")
                .tribunalCorrespondenceEmail("manchester@gmail.com")
                .build();
        glasgowDefaultValues = DefaultValues.builder()
                .positionType("Awaiting ET3")
                .claimantTypeOfClaimant("Individual")
                .managingOffice("Glasgow")
                .tribunalCorrespondenceAddressLine1("Eagle Building,")
                .tribunalCorrespondenceAddressLine2("215 Bothwell Street,")
                .tribunalCorrespondenceTown("Glasgow,")
                .tribunalCorrespondencePostCode("G2 7TS")
                .tribunalCorrespondenceTelephone("0141 204 0730")
                .tribunalCorrespondenceFax("2937126570")
                .tribunalCorrespondenceDX("1231123")
                .tribunalCorrespondenceEmail("glasgow@gmail.com")
                .build();
    }

    @Test(expected = Exception.class)
    public void caseCreationManchesterRequestException() throws IOException {
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenThrow(feignError());
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        when(defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, MANCHESTER_CASE_TYPE_ID)).thenReturn(manchesterDefaultValues);
        caseUpdateForCaseWorkerService.caseUpdateRequest(manchesterCcdRequest, "authToken");
    }

    @Test
    public void caseCreationManchesterRequest() throws IOException {
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(manchesterCcdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        when(defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, MANCHESTER_CASE_TYPE_ID)).thenReturn(manchesterDefaultValues);
        SubmitEvent submitEvent1 = caseUpdateForCaseWorkerService.caseUpdateRequest(manchesterCcdRequest, "authToken");
        assertEquals(submitEvent, submitEvent1);
    }

    @Test
    public void caseCreationGlasgowRequest() throws IOException {
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(glasgowCcdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        when(defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, GLASGOW_CASE_TYPE_ID)).thenReturn(glasgowDefaultValues);
        SubmitEvent submitEvent1 = caseUpdateForCaseWorkerService.caseUpdateRequest(glasgowCcdRequest, "authToken");
        assertEquals(submitEvent, submitEvent1);
    }
}