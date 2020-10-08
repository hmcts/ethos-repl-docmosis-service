package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

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

    @Autowired
    public MultipleSingleMidEventValidationService(SingleCasesReadingService singleCasesReadingService) {
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public void multipleSingleValidationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();

        String caseToSearch = multipleData.getBatchUpdateCase();

        if (isNullOrEmpty(caseToSearch)) {

            log.info("No adding any validation");

            return;

        }

        log.info("Validating the single case exists in the multiple");

        validateSingleCaseInMultiple(multipleData, errors, caseToSearch);

        if (errors.isEmpty()) {

            log.info("Populating dynamic lists");

            populateDynamicLists(userToken, multipleDetails.getCaseTypeId(), multipleData, caseToSearch);

        }

        log.info("End of validation");

    }

    private void validateSingleCaseInMultiple(MultipleData multipleData, List<String> errors, String caseToSearch) {

        if (multipleData.getCaseIdCollection() == null ||
                multipleData.getCaseIdCollection().isEmpty()) {

            log.info("Multiple does not have cases");

            errors.add("Multiple does not have cases");

        } else {

            log.info("Searching case: " + caseToSearch);

            if (searchCaseInCaseIdCollection(multipleData, caseToSearch)) {

                log.info("Case found");

            } else {

                log.info("Multiple does not have the case");

                errors.add("Multiple does not have the case: " + caseToSearch);

            }

        }

    }

    private boolean searchCaseInCaseIdCollection(MultipleData multipleData, String caseToSearch) {

        return multipleData.getCaseIdCollection().stream()
                .anyMatch(value -> value.getId() != null
                        && !value.getId().equals("null")
                        && value.getValue().getEthosCaseReference().equals(caseToSearch));

    }

    private void populateDynamicLists(String userToken, String caseTypeId,
                                      MultipleData multipleData, String caseToSearch) {

        SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(
                userToken,
                caseTypeId,
                caseToSearch);

        log.info("Reading case");

        log.info("Checking RepresentativeClaimantType");

        List<DynamicValueType> claimantDynamicList = new ArrayList<>();

        if (submitEvent.getCaseData().getRepresentativeClaimantType() != null) {

            log.info("RepresentativeClaimantType: " + submitEvent.getCaseData().getRepresentativeClaimantType());

            claimantDynamicList = new ArrayList<>(Collections.singletonList(
                    MultiplesHelper.getDynamicValue(
                            submitEvent.getCaseData().getRepresentativeClaimantType().getNameOfRepresentative())));

        }

        multipleData.setBatchUpdateClaimantRep(populateDynamicList(claimantDynamicList));

        log.info("BatchUpdateClaimantRep: " + multipleData.getBatchUpdateClaimantRep());

        log.info("Checking JurCodesCollection");

        List<DynamicValueType> jurCodesCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getJurCodesCollection() != null) {

            log.info("JurCodesCollection: " + submitEvent.getCaseData().getJurCodesCollection());

            jurCodesCollection = submitEvent.getCaseData().getJurCodesCollection().stream()
                    .map(jurCodesTypeItem -> MultiplesHelper.getDynamicValue(jurCodesTypeItem.getValue().getJuridictionCodesList()))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateJurisdiction(populateDynamicList(jurCodesCollection));

        log.info("BatchUpdateJurisdiction: " + multipleData.getBatchUpdateJurisdiction());

        log.info("Checking RespondentCollection");

        List<DynamicValueType> respondentCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getRespondentCollection() != null) {

            log.info("RespondentCollection: " + submitEvent.getCaseData().getRespondentCollection());

            respondentCollection = submitEvent.getCaseData().getRespondentCollection().stream()
                    .map(respondentSumTypeItem -> MultiplesHelper.getDynamicValue(respondentSumTypeItem.getValue().getRespondentName()))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateRespondent(populateDynamicList(respondentCollection));

        log.info("BatchUpdateRespondent: " + multipleData.getBatchUpdateRespondent());

    }

    private DynamicFixedListType populateDynamicList(List<DynamicValueType> listItems) {

        listItems.add(0, MultiplesHelper.getDynamicValue(SELECT_NONE_VALUE));

        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();

        dynamicFixedListType.setListItems(listItems);

        dynamicFixedListType.setValue(MultiplesHelper.getDynamicValue(SELECT_NONE_VALUE));

        return dynamicFixedListType;

    }

}
