package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
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

        multipleCloseEventValidationService.validateJurisdictionCollections(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
    }

    @Test
    public void multipleCloseEventValidationJurisdictionsCollectionReturnsErrors() {
        var jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId("TEST");
        jurCodesTypeItem.setValue(new JurCodesType());

        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("245004/2020");
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

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

        doCallRealMethod().when(eventValidationService).validateJurisdictionOutcome(isA(CaseData.class),
                eq(false), eq(true), eq(new ArrayList<>()));

        multipleCloseEventValidationService.validateJurisdictionCollections(
                userToken,
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("245004/2020 - " + MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE, errors.get(0));
    }

    public void multipleCloseEventValidationJurisdictionsCollectionNoError() {
        var jurCodeType = new JurCodesType();
        jurCodeType.setJudgmentOutcome("some outcome");
        var jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId("TEST");
        jurCodesTypeItem.setValue(jurCodeType);

        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("245004/2020");
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

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

        doCallRealMethod().when(eventValidationService).validateJurisdictionOutcome(isA(CaseData.class),
                eq(false), eq(true), eq(new ArrayList<>()));

        multipleCloseEventValidationService.validateJurisdictionCollections(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
    }

    private SubmitEvent getSubmitEventForCase(CaseData caseData) {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(CLOSED_STATE);
        submitEvent.setCaseId(1232121232);
        return submitEvent;
    }
}