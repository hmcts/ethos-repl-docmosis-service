package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleSingleMidEventValidationServiceTest {

    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private MultipleHelperService multipleHelperService;

    @InjectMocks
    private MultipleSingleMidEventValidationService multipleSingleMidEventValidationService;

    private MultipleDetails multipleDetails;
    private List<String> errors;
    private String userToken;
    private List<SubmitEvent> submitEventList;
    private List<String> caseIdCollection;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        submitEventList = MultipleUtil.getSubmitEvents();
        errors = new ArrayList<>();
        userToken = "authString";
        caseIdCollection = new ArrayList<>(Arrays.asList("21006/2020", "245000/2020", "245001/2020"));
    }

    @Test
    public void multipleSingleValidationLogic() {

        MultipleData multipleData = multipleDetails.getCaseData();
        multipleData.setBatchUpdateCase("245000/2020");

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleData.getBatchUpdateCase(),
                multipleData.getMultipleSource()))
                .thenReturn(submitEventList.getFirst());

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
            multipleData,
                errors))
                .thenReturn(caseIdCollection);

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
        assertEquals(SELECT_NONE_VALUE, multipleData.getBatchUpdateClaimantRep().getValue().getCode());
        assertEquals(SELECT_NONE_VALUE, multipleData.getBatchUpdateJurisdiction().getValue().getLabel());
        assertEquals(2, multipleData.getBatchUpdateRespondent().getListItems().size());
        assertEquals(SELECT_NONE_VALUE, multipleData.getBatchUpdateRespondentRep().getValue().getLabel());

    }

    @Test
    public void multipleSingleValidationLogicDoesNotExist() {

        multipleDetails.getCaseData().setBatchUpdateCase("245010/2020");

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleDetails.getCaseData(),
                errors))
                .thenReturn(caseIdCollection);

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Multiple does not have the case: 245010/2020", errors.getFirst());

    }

    @Test
    public void multipleSingleValidationLogicEmptyCaseIdCollection() {

        multipleDetails.getCaseData().setCaseIdCollection(null);
        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Multiple does not have cases", errors.getFirst());

    }

    @Test
    public void multipleSingleValidationLogicEmptyCaseSearch() {

        multipleDetails.getCaseData().setBatchUpdateCase(null);

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());

    }

    @Test
    public void multipleSingleValidationLogicWithDynamicLists() {

        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfRepresentative("Rep");
        submitEventList.getFirst().getCaseData().setRepresentativeClaimantType(representedTypeC);

        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("AA");
        jurCodesTypeItem.setValue(jurCodesType);
        submitEventList.getFirst().getCaseData().setJurCodesCollection(
            new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEventList.getFirst());

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleDetails.getCaseData(),
                errors))
                .thenReturn(caseIdCollection);

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateClaimantRep().getListItems().size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateJurisdiction().getListItems().size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateRespondent().getListItems().size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateRespondentRep().getListItems().size());

    }

    /**
     * This test is for the scenario where CCD returns the case data with a
     * representativeClaimantType as a RepresentedTypeC object with no values set.
     */
    @Test
    public void shouldHandleRepresentativeClaimantWithNoValues() {
        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        submitEventList.getFirst().getCaseData().setRepresentativeClaimantType(representedTypeC);

        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("AA");
        jurCodesTypeItem.setValue(jurCodesType);
        submitEventList.getFirst().getCaseData().setJurCodesCollection(
            new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEventList.getFirst());

        when(multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleDetails.getCaseData(),
                errors))
                .thenReturn(caseIdCollection);

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
        assertEquals(1, multipleDetails.getCaseData().getBatchUpdateClaimantRep().getListItems().size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateJurisdiction().getListItems().size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateRespondent().getListItems().size());
        assertEquals(2, multipleDetails.getCaseData().getBatchUpdateRespondentRep().getListItems().size());
    }

}