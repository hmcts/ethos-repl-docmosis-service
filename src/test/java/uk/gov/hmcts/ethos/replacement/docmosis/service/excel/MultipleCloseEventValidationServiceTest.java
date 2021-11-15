package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;

import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;

import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_LISTED_CASE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JUDGEMENT_JURISDICTION_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleCloseEventValidationServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;
    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private EventValidationService eventValidationService;
    @InjectMocks
    private MultipleCloseEventValidationService multipleCloseEventValidationService;

    private MultipleDetails multipleDetails;
    private List<String> caseIdCollection;
    private List<String> errors;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        errors = new ArrayList<>();
        userToken = "authString";
        caseIdCollection = new ArrayList<>(List.of("245000/2020"));
    }

    @Test
    public void multipleCloseEventValidationEmptyCaseIdCollection() {
        when(multipleHelperService.getEthosCaseRefCollection(
                userToken,
                multipleDetails.getCaseData(),
                errors)
        ).thenReturn(new ArrayList<>());

        List<String> errors = multipleCloseEventValidationService.validateCasesBeforeCloseEvent(
                                userToken,
                                multipleDetails);

        assertEquals(0, errors.size());
    }

    @Test
    public void multipleCloseEventValidationReturnsErrors() {
        var jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId("TEST");
        jurCodesTypeItem.setValue(new JurCodesType());

        var judgmentTypeItem = new JudgementTypeItem();
        judgmentTypeItem.setId("TEST");
        judgmentTypeItem.setValue(new JudgementType());

        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("245004/2020");
        caseData.setJurCodesCollection(List.of(jurCodesTypeItem));
        caseData.setJudgementCollection(List.of(judgmentTypeItem));

        var submitEvent = getSubmitEventForCase(caseData);

        multipleDetails.getCaseData().setLeadCase(null);

        when(multipleHelperService.getEthosCaseRefCollection(
                userToken,
                multipleDetails.getCaseData(),
                errors)
        ).thenReturn(caseIdCollection);

        when(singleCasesReadingService.retrieveSingleCases(
                userToken,
                multipleDetails.getCaseTypeId(),
                caseIdCollection,
                multipleDetails.getCaseData().getMultipleSource())
        ).thenReturn(new ArrayList<>(Collections.singletonList(submitEvent)));

        doCallRealMethod().when(eventValidationService).validateCaseBeforeCloseEvent(isA(CaseData.class),
                eq(false), eq(true), anyList());

        doCallRealMethod().when(eventValidationService).validateJurisdictionOutcome(isA(CaseData.class),
                eq(false), eq(true), anyList());

        doCallRealMethod().when(eventValidationService).validateJudgementsHasJurisdiction(isA(CaseData.class),
                eq(true), anyList());

        List<String> errors = multipleCloseEventValidationService.validateCasesBeforeCloseEvent(
                userToken,
                multipleDetails);

        assertEquals(2, errors.size());
        assertEquals("245004/2020 - " + MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    public void shouldReturnJurisdictionsOutcomeNotAllocatedErrors() {
        var caseData = getCaseData();
        caseData.getJurCodesCollection().clear();
  
        var jurCodesTypeItem = new JurCodesTypeItem();
        var jurCodeType = new JurCodesType();
        jurCodeType.setJudgmentOutcome("Not allocated");

        jurCodesTypeItem.setId("TEST");
        jurCodesTypeItem.setValue(jurCodeType);

        caseData.setEthosCaseReference("245004/2020");
        caseData.setJurCodesCollection(List.of(jurCodesTypeItem));
 
        var submitEvent = getSubmitEventForCase(caseData);

        when(multipleHelperService.getEthosCaseRefCollection(
                userToken,
                multipleDetails.getCaseData(),
                errors)
        ).thenReturn(caseIdCollection);

        when(singleCasesReadingService.retrieveSingleCases(
                userToken,
                multipleDetails.getCaseTypeId(),
                caseIdCollection,
                multipleDetails.getCaseData().getMultipleSource())

        ).thenReturn(new ArrayList<>(Collections.singletonList(submitEvent)));

        doCallRealMethod().when(eventValidationService).validateCaseBeforeCloseEvent(isA(CaseData.class),
                eq(false), eq(true), anyList());

        doCallRealMethod().when(eventValidationService).validateJurisdictionOutcome(isA(CaseData.class),
                eq(false), eq(true), anyList());

        doCallRealMethod().when(eventValidationService).validateJudgementsHasJurisdiction(isA(CaseData.class),
                eq(true), anyList());

        List<String> errors = multipleCloseEventValidationService.validateCasesBeforeCloseEvent(
                userToken,
                multipleDetails);

        assertEquals(1, errors.size());
        assertEquals("245004/2020 - " + JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    public void multipleCloseEventValidationListedHearingReturnsErrors() {
        var caseData = getCaseData();
        var hearings = caseData.getHearingCollection();
        var hearingDates = hearings.get(0).getValue().getHearingDateCollection();
        hearingDates.get(0).getValue().setHearingStatus("Listed");
        var submitEvent = getSubmitEventForCase(caseData);

        when(multipleHelperService.getEthosCaseRefCollection(
                userToken,
                multipleDetails.getCaseData(),
                errors)
        ).thenReturn(caseIdCollection);

        when(singleCasesReadingService.retrieveSingleCases(
                userToken,
                multipleDetails.getCaseTypeId(),
                caseIdCollection,
                multipleDetails.getCaseData().getMultipleSource())
        ).thenReturn(new ArrayList<>(Collections.singletonList(submitEvent)));

        doCallRealMethod().when(eventValidationService).validateHearingStatusForCaseCloseEvent((isA(CaseData.class)),
                eq(new ArrayList<>()));

        List<String> errors = multipleCloseEventValidationService.validateCasesBeforeCloseEvent(
                userToken,
                multipleDetails);

        assertEquals(1, errors.size());
        assertEquals(CLOSING_LISTED_CASE_ERROR, errors.get(0));
    }

    @Test
    public void multipleCloseEventValidationHeardHearingWithNoJudgeReturnsErrors() {
        var caseData = getCaseData();
        caseData.getHearingCollection().get(0).getValue().setJudge(null);
        var submitEvent = getSubmitEventForCase(caseData);

        when(multipleHelperService.getEthosCaseRefCollection(
                userToken,
                multipleDetails.getCaseData(),
                errors)
        ).thenReturn(caseIdCollection);

        when(singleCasesReadingService.retrieveSingleCases(
                userToken,
                multipleDetails.getCaseTypeId(),
                caseIdCollection,
                multipleDetails.getCaseData().getMultipleSource())
        ).thenReturn(new ArrayList<>(Collections.singletonList(submitEvent)));

        doCallRealMethod()
                .when(eventValidationService)
                .validateHearingJudgeAllocationForCaseCloseEvent((isA(CaseData.class)), eq(new ArrayList<>()));

        List<String> errors = multipleCloseEventValidationService.validateCasesBeforeCloseEvent(
                userToken,
                multipleDetails);

        assertEquals(1, errors.size());
        assertEquals(CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR, errors.get(0));
    }

    private CaseData getCaseData() {

        var jurCodeType = new JurCodesType();
        jurCodeType.setJudgmentOutcome("some outcome");
        var jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId("TEST");
        jurCodesTypeItem.setValue(jurCodeType);

        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("245004/2020");
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setId("123");

        var hearingType =  new HearingType();
        hearingType.setHearingNumber("1");
        hearingType.setJudge("Test Judge");
        hearingTypeItem.setValue(hearingType);

        DateListedTypeItem item = new DateListedTypeItem();
        item.setId("3435");

        var dateListedTypeThree = new DateListedType();
        dateListedTypeThree.setHearingStatus("Heard");
        dateListedTypeThree.setListedDate("13/07/2020");
        item.setValue(dateListedTypeThree);

        List<DateListedTypeItem> dateListedTypeItems = new ArrayList<>();
        dateListedTypeItems.add(item);
        hearingType.setHearingDateCollection(dateListedTypeItems);

        List<HearingTypeItem> hearings = new ArrayList<>();
        hearings.add(hearingTypeItem);
        caseData.setHearingCollection(hearings);

        return caseData;
    }

    private SubmitEvent getSubmitEventForCase(CaseData caseData) {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(CLOSED_STATE);
        submitEvent.setCaseId(1232121232);
        return submitEvent;
    }
}