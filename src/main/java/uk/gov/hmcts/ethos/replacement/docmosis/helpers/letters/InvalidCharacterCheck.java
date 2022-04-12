package uk.gov.hmcts.ethos.replacement.docmosis.helpers.letters;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

public class InvalidCharacterCheck {

    public static final String NEW_LINE_ERROR = " is split over 2 lines. Please correct this before "
            + "generating a letter";
    public static final String DOUBLE_SPACE_ERROR = " contains a double space. Please correct this before"
            + " generating a letter";

    private InvalidCharacterCheck() {
    }

    public static List<String> checkNamesForInvalidCharacters(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        List<String> nameOfParties = findAllParties(caseData);
        for (String name : nameOfParties) {
            if (!Strings.isNullOrEmpty(name) && name.contains("  ")) {
                errors.add(name + DOUBLE_SPACE_ERROR);
            }
            if (!Strings.isNullOrEmpty(name) && name.contains("\n")) {
                errors.add(name + NEW_LINE_ERROR);
            }
        }
        return errors;
    }

    private static List<String> findAllParties(CaseData caseData) {
        List<String> parties = new ArrayList<>();
        parties.add(caseData.getClaimant());
        if (YES.equals(caseData.getClaimantRepresentedQuestion())) {
            parties.add(caseData.getRepresentativeClaimantType().getNameOfRepresentative());
        }
        if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())) {
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                parties.add(respondentSumTypeItem.getValue().getRespondentName());
            }
        }
        if (CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
            for (RepresentedTypeRItem representedTypeRItem : caseData.getRepCollection()) {
                parties.add(representedTypeRItem.getValue().getNameOfRepresentative());
            }
        }

        return parties;
    }
}
