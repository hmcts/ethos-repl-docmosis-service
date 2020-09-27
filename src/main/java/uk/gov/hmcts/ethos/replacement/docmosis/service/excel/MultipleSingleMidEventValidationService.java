package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        log.info("Validating the single case exists in the multiple");

        validateSingleCaseInMultiple(multipleData, errors, caseToSearch);

        if (errors.isEmpty()) {

            log.info("Populating dynamic lists");

            populateDynamicLists(userToken, multipleDetails.getCaseTypeId(), multipleData, caseToSearch);

        }

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

        RepresentedTypeC representativeClaimantType = submitEvent.getCaseData().getRepresentativeClaimantType();

        multipleData.setBatchUpdateClaimantRep(
                populateDynamicList(getRepClaimantDynamicList(representativeClaimantType)));

        List<JurCodesTypeItem> jurCodesCollection = submitEvent.getCaseData().getJurCodesCollection();

        multipleData.setBatchUpdateJurisdiction(
                populateDynamicList(getJurCodesDynamicList(jurCodesCollection)));

        List<RespondentSumTypeItem> respondentCollection = submitEvent.getCaseData().getRespondentCollection();

        multipleData.setBatchUpdateRespondent(
                populateDynamicList(getRespondentDynamicList(respondentCollection)));

    }

    private List<DynamicValueType> getRepClaimantDynamicList(RepresentedTypeC representativeClaimantType) {

        if (representativeClaimantType == null) {

            return new ArrayList<>();

        } else {

            return new ArrayList<>(Collections.singletonList(
                    MultiplesHelper.getDynamicValue(representativeClaimantType.getNameOfRepresentative())));

        }
    }

    private List<DynamicValueType> getJurCodesDynamicList(List<JurCodesTypeItem> jurCodesCollection) {

        List<DynamicValueType> listItems = new ArrayList<>();

        if (jurCodesCollection != null) {

            listItems = jurCodesCollection.stream()
                    .map(jurCodesTypeItem -> MultiplesHelper.getDynamicValue(jurCodesTypeItem.getValue().getJuridictionCodesList()))
                    .collect(Collectors.toList());

        }

        return listItems;

    }

    private List<DynamicValueType> getRespondentDynamicList(List<RespondentSumTypeItem> respondentCollection) {

        List<DynamicValueType> listItems = new ArrayList<>();

        if (respondentCollection != null) {

            listItems = respondentCollection.stream()
                    .map(respondentSumTypeItem -> MultiplesHelper.getDynamicValue(respondentSumTypeItem.getValue().getRespondentName()))
                    .collect(Collectors.toList());

        }

        return listItems;

    }

    private DynamicFixedListType populateDynamicList(List<DynamicValueType> listItems) {

        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();

        if (!listItems.isEmpty()) {

            dynamicFixedListType.setListItems(listItems);

            //Default dynamic list
            dynamicFixedListType.setValue(listItems.get(0));

        }

        return dynamicFixedListType;

    }

}
