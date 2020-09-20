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
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

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

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        multipleDetails.setCaseId("12121212");
        caseDetails = new CaseDetails();
        caseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        String oldMultipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());
        multipleCaseTypeId = oldMultipleCaseTypeId.substring(0, oldMultipleCaseTypeId.length() - 1);
        caseDetails.setCaseData(MultipleUtil.getCaseDataWithSingleMoveCases());
        caseDetails.setCaseId("12321321");
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        userToken = "authString";
    }

    @Test
    public void addSingleCaseToMultipleLogicLead() {

        List<String> errors = new ArrayList<>();
        String updatedSubMultipleName = caseDetails.getCaseData().getMoveCases().getUpdatedSubMultipleName();

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                caseDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(submitMultipleEvents);

        addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(userToken,
                caseDetails,
                errors);

        verify(multipleHelperService, times(1)).addLeadMarkUp(
                userToken,
                multipleCaseTypeId,
                submitMultipleEvents.get(0).getCaseData(),
                caseDetails.getCaseData().getEthosCaseReference(),
                caseDetails.getCaseId());

        verify(multipleHelperService, times(1)).moveCasesAndSendUpdateToMultiple(
                userToken,
                updatedSubMultipleName,
                caseDetails.getJurisdiction(),
                multipleCaseTypeId,
                String.valueOf(submitMultipleEvents.get(0).getCaseId()),
                submitMultipleEvents.get(0).getCaseData(),
                new ArrayList<>(Collections.singletonList(caseDetails.getCaseData().getEthosCaseReference())),
                new ArrayList<>());

        verify(multipleHelperService, times(1)).sendCreationUpdatesToSinglesNoConfirmation(
                userToken,
                multipleCaseTypeId,
                caseDetails.getJurisdiction(),
                submitMultipleEvents.get(0).getCaseData(),
                new ArrayList<>(),
                new ArrayList<>(Collections.singletonList(
                        MultiplesHelper.getLeadFromCaseIds(submitMultipleEvents.get(0).getCaseData()))),
                "");

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(MULTIPLE_CASE_TYPE, caseDetails.getCaseData().getCaseType());
        assertEquals("246000", caseDetails.getCaseData().getMultipleReference());
        assertEquals(YES, caseDetails.getCaseData().getLeadClaimant());

    }

    @Test
    public void addSingleCaseToMultipleLogicNoLead() {

    caseDetails.getCaseData().getMoveCases().setLeadCase(NO);

        List<String> errors = new ArrayList<>();
        String updatedSubMultipleName = caseDetails.getCaseData().getMoveCases().getUpdatedSubMultipleName();

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                caseDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(submitMultipleEvents);

        addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(userToken,
                caseDetails,
                errors);

        verify(multipleHelperService, times(0)).addLeadMarkUp(
                userToken,
                multipleCaseTypeId,
                submitMultipleEvents.get(0).getCaseData(),
                caseDetails.getCaseId(),
                "");

        verify(multipleHelperService, times(1)).moveCasesAndSendUpdateToMultiple(
                userToken,
                updatedSubMultipleName,
                caseDetails.getJurisdiction(),
                multipleCaseTypeId,
                String.valueOf(submitMultipleEvents.get(0).getCaseId()),
                submitMultipleEvents.get(0).getCaseData(),
                new ArrayList<>(Collections.singletonList(caseDetails.getCaseData().getEthosCaseReference())),
                new ArrayList<>());

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(MULTIPLE_CASE_TYPE, caseDetails.getCaseData().getCaseType());
        assertEquals("246000", caseDetails.getCaseData().getMultipleReference());
        assertEquals(NO, caseDetails.getCaseData().getLeadClaimant());

    }

    @Test
    public void addSingleCaseToMultipleLogicNoLeadButWithEmptyMultiple() {

        caseDetails.getCaseData().getMoveCases().setLeadCase(NO);
        submitMultipleEvents.get(0).getCaseData().setCaseIdCollection(null);
        String updatedSubMultipleName = caseDetails.getCaseData().getMoveCases().getUpdatedSubMultipleName();

        List<String> errors = new ArrayList<>();

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                caseDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(submitMultipleEvents);

        addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(userToken,
                caseDetails,
                errors);

        verify(multipleHelperService, times(1)).addLeadMarkUp(
                userToken,
                multipleCaseTypeId,
                submitMultipleEvents.get(0).getCaseData(),
                caseDetails.getCaseData().getEthosCaseReference(),
                caseDetails.getCaseId());

        verify(multipleHelperService, times(1)).moveCasesAndSendUpdateToMultiple(
                userToken,
                updatedSubMultipleName,
                caseDetails.getJurisdiction(),
                multipleCaseTypeId,
                String.valueOf(submitMultipleEvents.get(0).getCaseId()),
                submitMultipleEvents.get(0).getCaseData(),
                new ArrayList<>(Collections.singletonList(caseDetails.getCaseData().getEthosCaseReference())),
                new ArrayList<>());

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(MULTIPLE_CASE_TYPE, caseDetails.getCaseData().getCaseType());
        assertEquals("246000", caseDetails.getCaseData().getMultipleReference());
        assertEquals(YES, caseDetails.getCaseData().getLeadClaimant());

    }

}