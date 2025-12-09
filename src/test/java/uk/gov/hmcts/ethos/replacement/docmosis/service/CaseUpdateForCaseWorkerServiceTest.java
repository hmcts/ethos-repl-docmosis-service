package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

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
    private CaseDetails manchesterCaseDetails;
    private CaseDetails glasgowCaseDetails;

    @Before
    public void setUp() {
        submitEvent = new SubmitEvent();

        manchesterCcdRequest = new CCDRequest();
        manchesterCaseDetails = new CaseDetails();
        manchesterCaseDetails.setCaseData(new CaseData());
        manchesterCaseDetails.setCaseId("123456");
        manchesterCaseDetails.setCaseTypeId(MANCHESTER_DEV_CASE_TYPE_ID);
        manchesterCaseDetails.setJurisdiction("TRIBUNALS");
        manchesterCcdRequest.setCaseDetails(manchesterCaseDetails);

        glasgowCcdRequest = new CCDRequest();
        glasgowCaseDetails = new CaseDetails();
        glasgowCaseDetails.setCaseData(new CaseData());
        glasgowCaseDetails.setCaseId("123456");
        glasgowCaseDetails.setCaseTypeId(SCOTLAND_DEV_CASE_TYPE_ID);
        glasgowCaseDetails.setJurisdiction("TRIBUNALS");
        glasgowCcdRequest.setCaseDetails(glasgowCaseDetails);

        caseUpdateForCaseWorkerService = new CaseUpdateForCaseWorkerService(ccdClient, defaultValuesReaderService);
        manchesterDefaultValues = DefaultValues.builder()
                .positionType("Awaiting ET3")
                .claimantTypeOfClaimant(INDIVIDUAL_TYPE_CLAIMANT)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Manchester Employment Tribunal,")
                .tribunalCorrespondenceAddressLine2("Alexandra House,")
                .tribunalCorrespondenceAddressLine3("14-22 The Parsonage,")
                .tribunalCorrespondenceTown("Manchester,")
                .tribunalCorrespondencePostCode("M3 2JA")
                .tribunalCorrespondenceTelephone("0300 323 0196")
                .tribunalCorrespondenceFax("7577126570")
                .tribunalCorrespondenceDX("123456")
                .tribunalCorrespondenceEmail("manchester@gmail.com")
                .build();
        glasgowDefaultValues = DefaultValues.builder()
                .positionType("Awaiting ET3")
                .claimantTypeOfClaimant(INDIVIDUAL_TYPE_CLAIMANT)
                .managingOffice(GLASGOW_OFFICE)
                .caseType(SINGLE_CASE_TYPE)
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
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        when(defaultValuesReaderService.getDefaultValues( "", manchesterCaseDetails.getCaseTypeId())).thenReturn(manchesterDefaultValues);
        caseUpdateForCaseWorkerService.caseUpdateRequest(manchesterCcdRequest, "authToken");
    }

    @Test
    public void caseCreationManchesterRequest() throws IOException {
        manchesterCcdRequest.getCaseDetails().getCaseData().setManagingOffice("Manchester");
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(manchesterCcdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        when(defaultValuesReaderService.getDefaultValues("Manchester", manchesterCaseDetails.getCaseTypeId())).thenReturn(manchesterDefaultValues);
        SubmitEvent submitEvent1 = caseUpdateForCaseWorkerService.caseUpdateRequest(manchesterCcdRequest, "authToken");
        assertEquals(submitEvent, submitEvent1);
    }

    @Test
    public void caseCreationGlasgowRequest() throws IOException {
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(glasgowCcdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenReturn(submitEvent);
        when(defaultValuesReaderService.getDefaultValues( "", glasgowCaseDetails.getCaseTypeId())).thenReturn(glasgowDefaultValues);
        SubmitEvent submitEvent1 = caseUpdateForCaseWorkerService.caseUpdateRequest(glasgowCcdRequest, "authToken");
        assertEquals(submitEvent, submitEvent1);
    }
}