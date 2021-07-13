package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import static com.google.common.base.Strings.isNullOrEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;

import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

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

        var multipleData = multipleDetails.getCaseData();

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

    private void validateSingleCaseInMultiple(String userToken, MultipleData multipleData, List<String> errors,
                                              String caseToSearch) {

        List<String> ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleData, errors);

        if (ethosCaseRefCollection == null
                || ethosCaseRefCollection.isEmpty()) {

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

        var submitEvent = singleCasesReadingService.retrieveSingleCase(
                userToken,
                caseTypeId,
                caseToSearch,
                multipleData.getMultipleSource());

        log.info("Checking RepresentativeClaimantType");

        List<DynamicValueType> claimantDynamicList = new ArrayList<>();

        if (hasRepresentativeClaimant(submitEvent.getCaseData())) {

            claimantDynamicList = new ArrayList<>(Collections.singletonList(
                    Helper.getDynamicValue(
                            submitEvent.getCaseData().getRepresentativeClaimantType().getNameOfRepresentative())));

        }

        multipleData.setBatchUpdateClaimantRep(populateDynamicList(claimantDynamicList));

        log.info("Checking JurCodesCollection");

        List<DynamicValueType> jurCodesCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getJurCodesCollection() != null) {

            jurCodesCollection = submitEvent.getCaseData().getJurCodesCollection().stream()
                    .map(jurCodesTypeItem ->
                            Helper.getDynamicValue(jurCodesTypeItem.getValue().getJuridictionCodesList()))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateJurisdiction(populateDynamicList(jurCodesCollection));

        log.info("Checking RespondentCollection");

        List<DynamicValueType> respondentCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getRespondentCollection() != null) {

            respondentCollection = submitEvent.getCaseData().getRespondentCollection().stream()
                    .map(respondentSumTypeItem ->
                            Helper.getDynamicValue(respondentSumTypeItem.getValue().getRespondentName()))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateRespondent(populateDynamicList(respondentCollection));

        log.info("Checking JudgementCollection");

        List<DynamicValueType> judgementCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getJudgementCollection() != null
                && !submitEvent.getCaseData().getJudgementCollection().isEmpty()) {

            for (var i = 0; i < submitEvent.getCaseData().getJudgementCollection().size(); i++) {
                var judgementTypeItem = submitEvent.getCaseData().getJudgementCollection().get(i);
                judgementCollection.add(Helper.getDynamicCodeLabel(
                        judgementTypeItem.getId(), i
                                + " - " + judgementTypeItem.getValue().getJudgementType()
                                + " - " + judgementTypeItem.getValue().getDateJudgmentMade()));
            }
        }

        multipleData.setBatchUpdateJudgment(populateDynamicList(judgementCollection));

        log.info("Checking RepCollection");

        List<DynamicValueType> repCollection = new ArrayList<>();

        if (submitEvent.getCaseData().getRepCollection() != null) {

            repCollection = submitEvent.getCaseData().getRepCollection().stream()
                    .map(representedTypeRItem ->
                            Helper.getDynamicCodeLabel(representedTypeRItem.getId(),
                                    representedTypeRItem.getValue().getNameOfRepresentative()
                                            + " ("
                                            + representedTypeRItem.getValue().getRespRepName()
                                            + ")" ))
                    .collect(Collectors.toList());

        }

        multipleData.setBatchUpdateRespondentRep(populateDynamicList(repCollection));

    }

    private boolean hasRepresentativeClaimant(CaseData caseData) {
        var representativeClaimantType = caseData.getRepresentativeClaimantType();
        return representativeClaimantType != null && representativeClaimantType.getNameOfRepresentative() != null;
    }

    private DynamicFixedListType populateDynamicList(List<DynamicValueType> listItems) {

        listItems.add(0, Helper.getDynamicValue(SELECT_NONE_VALUE));

        var dynamicFixedListType = new DynamicFixedListType();

        dynamicFixedListType.setListItems(listItems);

        dynamicFixedListType.setValue(Helper.getDynamicValue(SELECT_NONE_VALUE));

        return dynamicFixedListType;

    }

}
