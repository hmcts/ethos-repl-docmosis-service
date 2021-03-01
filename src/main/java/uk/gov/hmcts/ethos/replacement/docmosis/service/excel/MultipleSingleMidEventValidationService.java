package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;

@Slf4j
@Service("multipleSingleMidEventValidationService")
public class MultipleSingleMidEventValidationService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleSingleMidEventValidationService(SingleCasesReadingService singleCasesReadingService,
                                                   MultipleHelperService multipleHelperService) {
        this.singleCasesReadingService = singleCasesReadingService;
        this.multipleHelperService = multipleHelperService;
    }

    public void multipleSingleValidationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();

        String caseToSearch = multipleData.getBatchUpdateCase();

        if (isNullOrEmpty(caseToSearch)) {

            log.info("No adding any validation");

            return;

        }

        log.info("Validating the single case exists in the multiple");

        validateSingleCaseInMultiple(userToken, multipleData, errors, caseToSearch);

        if (errors.isEmpty()) {

            populateDynamicLists(userToken, multipleDetails.getCaseTypeId(), multipleData, caseToSearch);

        }

    }

    private void validateSingleCaseInMultiple(String userToken, MultipleData multipleData, List<String> errors, String caseToSearch) {

        List<String> ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken, multipleData, errors);

        if (ethosCaseRefCollection == null ||
                ethosCaseRefCollection.isEmpty()) {

            log.info("Multiple does not have cases");

            errors.add("Multiple does not have cases");

        } else {

            log.info("Searching case: " + caseToSearch);

            if (searchCaseInCaseIdCollection(ethosCaseRefCollection, caseToSearch)) {

                log.info("Case found");

            } else {

                log.info("Multiple does not have the case");

                errors.add("Multiple does not have the case: " + caseToSearch);

            }

        }

    }

    private boolean searchCaseInCaseIdCollection(List<String> ethosCaseRefCollection, String caseToSearch) {

        return ethosCaseRefCollection.stream()
                .anyMatch(caseId -> caseId.equals(caseToSearch));

    }

    private void populateDynamicLists(String userToken, String caseTypeId,
                                      MultipleData multipleData, String caseToSearch) {

        SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(
                userToken,
                caseTypeId,
                caseToSearch,
                multipleData.getMultipleSource());

        log.info("Checking RepresentativeClaimantType");

        List<DynamicValueType> claimantDynamicList = new ArrayList<>();

        if (submitEvent.getCaseData().getRepresentativeClaimantType() != null) {

            claimantDynamicList = new ArrayList<>(Collections.singletonList(
                    Helper.getDynamicValue(
                            submitEvent.getCaseData().getRepresentativeClaimantType().getNameOfRepresentative())));

        }

        multipleData.setBatchUpdateClaimantRep(populateDynamicList(claimantDynamicList));

        log.info("Checking JurCodesCollection");

        List<DynamicValueType> jurCodesCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getJurCodesCollection() != null) {

            jurCodesCollection = submitEvent.getCaseData().getJurCodesCollection().stream()
                    .map(jurCodesTypeItem -> Helper.getDynamicValue(jurCodesTypeItem.getValue().getJuridictionCodesList()))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateJurisdiction(populateDynamicList(jurCodesCollection));

        log.info("Checking RespondentCollection");

        List<DynamicValueType> respondentCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getRespondentCollection() != null) {

            respondentCollection = submitEvent.getCaseData().getRespondentCollection().stream()
                    .map(respondentSumTypeItem -> Helper.getDynamicValue(respondentSumTypeItem.getValue().getRespondentName()))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateRespondent(populateDynamicList(respondentCollection));

    }

    private DynamicFixedListType populateDynamicList(List<DynamicValueType> listItems) {

        listItems.add(0, Helper.getDynamicValue(SELECT_NONE_VALUE));

        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();

        dynamicFixedListType.setListItems(listItems);

        dynamicFixedListType.setValue(Helper.getDynamicValue(SELECT_NONE_VALUE));

        return dynamicFixedListType;

    }

}
