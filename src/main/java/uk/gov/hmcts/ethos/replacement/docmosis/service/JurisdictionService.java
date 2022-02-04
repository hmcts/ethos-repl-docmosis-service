package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_DELETED_ERROR;

@Slf4j
@Service("jurisdictionService")
public class JurisdictionService {

    public void populateJurisdictionCode(CaseData caseData) {
        if (CollectionUtils.isNotEmpty(caseData.getJurCodesCollection())) {
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                var jurCodesType = jurCodesTypeItem.getValue();
                jurCodesType.setJurisdictionCode(jurCodesType.getJuridictionCodesList());
            }
        }
    }

    public void validateJurisdictionCodes(CaseData caseData, List<String> errors) {
        populateJurisdictionCodesList(caseData);
        validateDuplicatedJurisdictionCodes(caseData, errors);
        validateJurisdictionCodesExistenceInJudgement(caseData, errors);
    }

    private void populateJurisdictionCodesList(CaseData caseData) {
        if (CollectionUtils.isNotEmpty(caseData.getJurCodesCollection())) {
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                var jurCodesType = jurCodesTypeItem.getValue();
                if (isNullOrEmpty(jurCodesType.getJuridictionCodesList())) {
                    jurCodesType.setJuridictionCodesList(jurCodesType.getJurisdictionCode());
                }
            }
        }
    }

    private void validateJurisdictionCodesExistenceInJudgement(CaseData caseData, List<String> errors) {

        Set<String> jurCodesCollectionWithinJudgement = new HashSet<>();
        List<String> jurCodesCollection = Helper.getJurCodesCollection(caseData.getJurCodesCollection());
        if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                jurCodesCollectionWithinJudgement.addAll(
                        Helper.getJurCodesCollection(judgementTypeItem.getValue().getJurisdictionCodes()));
            }
        }
        log.info("Check if all jurCodesCollectionWithinJudgement are in jurCodesCollection");
        Set<String> result = jurCodesCollectionWithinJudgement.stream()
                .distinct()
                .filter(jurCode -> !jurCodesCollection.contains(jurCode))
                .collect(Collectors.toSet());

        if (!result.isEmpty()) {
            log.info("jurCodesCollectionWithinJudgement are not in jurCodesCollection: " + result);
            errors.add(JURISDICTION_CODES_DELETED_ERROR + result);
        }
    }

    private void validateDuplicatedJurisdictionCodes(CaseData caseData, List<String> errors) {
        if (CollectionUtils.isNotEmpty(caseData.getJurCodesCollection())) {
            var counter = 0;
            Set<String> uniqueCodes = new HashSet<>();
            List<String> duplicateCodes = new ArrayList<>();
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                counter++;
                String code = jurCodesTypeItem.getValue().getJuridictionCodesList();
                if (!uniqueCodes.add(code)) {
                    duplicateCodes.add(" \"" + code + "\" " + "in Jurisdiction" + " " + counter + " ");
                }
            }
            if (!duplicateCodes.isEmpty()) {
                errors.add(DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE + StringUtils.join(duplicateCodes, '-'));
            }
        }
    }
}
