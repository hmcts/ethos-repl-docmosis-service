package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;

public class HearingsHelper {
    private HearingsHelper() {
    }

    public static final String HEARING_CREATION_NUMBER_ERROR = "A new hearing can only "
            + "be added from the List Hearing menu item";
    public static final String HEARING_CREATION_DAY_ERROR = "A new day for a hearing can "
            + "only be added from the List Hearing menu item";

    public static String findHearingNumber(CaseData caseData, String hearingDate) {
        if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                    var listedDate = dateListedTypeItem.getValue().getListedDate().substring(0, 10);
                    if (listedDate.equals(hearingDate)) {
                        return hearingTypeItem.getValue().getHearingNumber();
                    }
                }
            }
        }
        return null;
    }

    static boolean isHearingStatusPostponed(DateListedType dateListedType) {
        return dateListedType.getHearingStatus() != null
                && dateListedType.getHearingStatus().equals(HEARING_STATUS_POSTPONED);
    }

    public static List<String> hearingMidEventValidation(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingNumber() == null
                        || hearingTypeItem.getValue().getHearingNumber().isEmpty()) {
                    errors.add(HEARING_CREATION_NUMBER_ERROR);
                    return errors;
                }
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        if (dateListedTypeItem.getValue().getListedDate() == null
                                || dateListedTypeItem.getValue().getListedDate().isEmpty()) {
                            errors.add(HEARING_CREATION_DAY_ERROR);
                            return  errors;
                        }
                    }
                }
            }
        }
        return errors;
    }

    public static void updatePostponedDate(CaseData caseData) {
        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        var dateListedType = dateListedTypeItem.getValue();
                        if (isHearingStatusPostponed(dateListedType) && dateListedType.getPostponedDate() == null) {
                            dateListedType.setPostponedDate(UtilHelper.formatCurrentDate2(LocalDate.now()));
                        }
                        if (dateListedType.getPostponedDate() != null
                                &&
                                (!isHearingStatusPostponed(dateListedType)
                                        || dateListedType.getHearingStatus() == null)) {
                            dateListedType.setPostponedDate(null);
                        }
                    }
                }
            }
        }

    }
}
