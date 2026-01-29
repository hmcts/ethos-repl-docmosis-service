package uk.gov.hmcts.ethos.replacement.docmosis.helpers.letters;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.ListingType;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

public class InvalidCharacterCheck {

    public static final String NEW_LINE_ERROR = "%s is split over 2 lines for case %s. Please correct this before "
            + "for the %s";
    public static final String DOUBLE_SPACE_ERROR = "%s contains a double space for case %s. Please correct this before"
            + " for the %s";

    private InvalidCharacterCheck() {
    }

    public static List<String> checkNamesForInvalidCharacters(CaseData caseData, String type) {
        List<String> nameOfParties = findAllParties(caseData);
        return addInvalidCharsErrors(nameOfParties, caseData.getEthosCaseReference(), type);
    }

    public static List<String> addInvalidCharsErrors(List<String> nameOfParties, String caseNo, String type) {
        List<String> errors = new ArrayList<>();
        for (String name : nameOfParties) {
            if (!Strings.isNullOrEmpty(name)) {
                if (name.contains("  ")) {
                    errors.add(String.format(DOUBLE_SPACE_ERROR, name, caseNo, type));
                }
                if (name.contains("\n")) {
                    errors.add(String.format(NEW_LINE_ERROR, name, caseNo, type));
                }
            }
        }
        return errors;
    }

    private static List<String> findAllParties(CaseData caseData) {
        List<String> parties = new ArrayList<>();
        parties.add("Claimant " + caseData.getClaimant());
        if (YES.equals(caseData.getClaimantRepresentedQuestion())) {
            parties.add("Claimant Rep " + caseData.getRepresentativeClaimantType().getNameOfRepresentative());
        }
        if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())) {
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                parties.add("Respondent " + respondentSumTypeItem.getValue().getRespondentName());
            }
        }
        if (CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
            for (RepresentedTypeRItem representedTypeRItem : caseData.getRepCollection()) {
                parties.add("Respondent Rep " + representedTypeRItem.getValue().getNameOfRepresentative());
            }
        }
        return parties;
    }

    private static List<String> checkNamesForInvalidCharactersListingType(ListingType listingType) {
        List<String> nameOfParties = findAllListingTypeParties(listingType);
        return addInvalidCharsErrors(nameOfParties, listingType.getElmoCaseReference(), "cause list");
    }

    private static List<String> findAllListingTypeParties(ListingType listingType) {
        List<String> parties = new ArrayList<>();
        if (!Strings.isNullOrEmpty(listingType.getRespondent())
                && !listingType.getRespondent().isBlank()) {
            parties.add("Respondent " + listingType.getRespondent());
        }
        if (!Strings.isNullOrEmpty(listingType.getClaimantName())
                && !listingType.getClaimantName().isBlank()) {
            parties.add("Claimant " + listingType.getClaimantName());
        }
        if (!Strings.isNullOrEmpty(listingType.getRespondentRepresentative())
                && !listingType.getRespondentRepresentative().isBlank()) {
            parties.add("Respondent Rep " + listingType.getRespondentRepresentative());
        }
        if (!Strings.isNullOrEmpty(listingType.getClaimantRepresentative())
                && !listingType.getClaimantRepresentative().isBlank()) {
            parties.add("Claimant Rep " + listingType.getClaimantRepresentative());
        }
        return parties;
    }

    public static boolean invalidCharactersExistAllListingTypes(ListingDetails listingDetails, List<String> errors) {
        List<ListingTypeItem> listingTypeItems = listingDetails.getCaseData().getListingCollection();
        List<String> invalidCharErrors = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listingTypeItems)) {
            for (ListingTypeItem listingTypeItem : listingTypeItems) {
                ListingType listingType = listingTypeItem.getValue();
                invalidCharErrors.addAll(checkNamesForInvalidCharactersListingType(listingType));
            }
            if (CollectionUtils.isEmpty(invalidCharErrors)) {
                return false;
            } else {
                errors.addAll(invalidCharErrors);
                return true;
            }

        } else {
            return false;
        }
    }
}
