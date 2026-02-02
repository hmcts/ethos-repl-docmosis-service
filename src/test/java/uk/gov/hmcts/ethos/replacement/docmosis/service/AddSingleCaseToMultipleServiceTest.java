package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class AddSingleCaseToMultipleServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;
    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @InjectMocks
    private AddSingleCaseToMultipleService addSingleCaseToMultipleService;

    private CaseDetails caseDetails;
    private String userToken;
    private String multipleCaseTypeId;
    private MultipleDetails multipleDetails;
    private List<SubmitMultipleEvent> submitMultipleEvents;
    private List<String> caseIdCollection;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        multipleDetails.setCaseId("12121212");
        caseDetails = new CaseDetails();
        caseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        multipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());
        caseDetails.setCaseData(MultipleUtil.getCaseDataForSinglesToBeMoved());
        caseDetails.setCaseId("12321321");
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        submitMultipleEvents.getFirst().setCaseId(12121212);
        caseIdCollection = new ArrayList<>(Arrays.asList("21006/2020", "245000/2020", "245001/2020"));
        userToken = "authString";

    }

    @Test
    public void addSingleCaseToMultipleLogicLead() {

        List<String> errors = new ArrayList<>();

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                caseDetails.getCaseData().getMultipleReference())
        ).thenReturn(submitMultipleEvents);

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
                submitMultipleEvents.getFirst().getCaseData(),
                errors)
        ).thenReturn(caseIdCollection);

        addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(userToken,
                caseDetails.getCaseData(),
                caseDetails.getCaseTypeId(),
                caseDetails.getJurisdiction(),
                caseDetails.getCaseId(),
                errors);

        verify(multipleHelperService, times(1)).getEthosCaseRefCollection(
                userToken,
                submitMultipleEvents.getFirst().getCaseData(),
                errors);

        verify(multipleHelperService, times(1))
            .sendCreationUpdatesToSinglesWithoutConfirmation(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                submitMultipleEvents.getFirst().getCaseData(),
                errors,
                new ArrayList<>(Collections.singletonList("21006/2020")),
                "",
                multipleDetails.getCaseId());

        verify(multipleHelperService, times(1)).addLeadMarkUp(
                userToken,
                multipleCaseTypeId,
                submitMultipleEvents.getFirst().getCaseData(),
                caseDetails.getCaseData().getEthosCaseReference(),
                caseDetails.getCaseId());
        
        verify(multipleHelperService, times(1)).moveCasesAndSendUpdateToMultiple(
                userToken,
            caseDetails.getCaseData().getSubMultipleName(),
                caseDetails.getJurisdiction(),
                multipleCaseTypeId,
                String.valueOf(submitMultipleEvents.getFirst().getCaseId()),
                submitMultipleEvents.getFirst().getCaseData(),
                new ArrayList<>(Collections.singletonList(caseDetails.getCaseData().getEthosCaseReference())),
                new ArrayList<>());

        assertEquals(MULTIPLE_CASE_TYPE, caseDetails.getCaseData().getEcmCaseType());
        assertEquals("246000", caseDetails.getCaseData().getMultipleReference());
        assertEquals(YES, caseDetails.getCaseData().getLeadClaimant());
    }

    @Test
    public void addSingleCaseToMultipleLogicNoLead() {

        caseDetails.getCaseData().setLeadClaimant(NO);

        List<String> errors = new ArrayList<>();

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                caseDetails.getCaseData().getMultipleReference())
        ).thenReturn(submitMultipleEvents);

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
                submitMultipleEvents.getFirst().getCaseData(),
                errors)
        ).thenReturn(caseIdCollection);

        addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(userToken,
                caseDetails.getCaseData(),
                caseDetails.getCaseTypeId(),
                caseDetails.getJurisdiction(),
                caseDetails.getCaseId(),
                errors);

        verify(multipleHelperService, times(1)).getEthosCaseRefCollection(
                userToken,
                submitMultipleEvents.getFirst().getCaseData(),
                errors);

        verify(multipleHelperService, times(1)).moveCasesAndSendUpdateToMultiple(
                userToken,
                caseDetails.getCaseData().getSubMultipleName(),
                caseDetails.getJurisdiction(),
                multipleCaseTypeId,
                String.valueOf(submitMultipleEvents.getFirst().getCaseId()),
                submitMultipleEvents.getFirst().getCaseData(),
                new ArrayList<>(Collections.singletonList(caseDetails.getCaseData().getEthosCaseReference())),
                new ArrayList<>());

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(MULTIPLE_CASE_TYPE, caseDetails.getCaseData().getEcmCaseType());
        assertEquals("246000", caseDetails.getCaseData().getMultipleReference());
        assertEquals(NO, caseDetails.getCaseData().getLeadClaimant());
    }

    @Test
    public void addSingleCaseToMultipleLogicNoLeadButWithEmptyMultiple() {

        caseDetails.getCaseData().setLeadClaimant(NO);
        submitMultipleEvents.getFirst().getCaseData().setCaseIdCollection(null);
        submitMultipleEvents.getFirst().getCaseData().setLeadCase(null);

        List<String> errors = new ArrayList<>();

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                caseDetails.getCaseData().getMultipleReference())
        ).thenReturn(submitMultipleEvents);

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
                submitMultipleEvents.getFirst().getCaseData(),
                errors)
        ).thenReturn(new ArrayList<>());

        addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(userToken,
                caseDetails.getCaseData(),
                caseDetails.getCaseTypeId(),
                caseDetails.getJurisdiction(),
                caseDetails.getCaseId(),
                errors);

        verify(multipleHelperService, times(1)).getEthosCaseRefCollection(
                userToken,
                submitMultipleEvents.getFirst().getCaseData(),
                errors);

        verify(multipleHelperService, times(1)).addLeadMarkUp(
                userToken,
                multipleCaseTypeId,
                submitMultipleEvents.getFirst().getCaseData(),
                caseDetails.getCaseData().getEthosCaseReference(),
                caseDetails.getCaseId());

        verify(multipleHelperService, times(1)).moveCasesAndSendUpdateToMultiple(
                userToken,
                caseDetails.getCaseData().getSubMultipleName(),
                caseDetails.getJurisdiction(),
                multipleCaseTypeId,
                String.valueOf(submitMultipleEvents.getFirst().getCaseId()),
                submitMultipleEvents.getFirst().getCaseData(),
                new ArrayList<>(Collections.singletonList(caseDetails.getCaseData().getEthosCaseReference())),
                new ArrayList<>());

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(MULTIPLE_CASE_TYPE, caseDetails.getCaseData().getEcmCaseType());
        assertEquals("246000", caseDetails.getCaseData().getMultipleReference());
        assertEquals(YES, caseDetails.getCaseData().getLeadClaimant());

    }

}