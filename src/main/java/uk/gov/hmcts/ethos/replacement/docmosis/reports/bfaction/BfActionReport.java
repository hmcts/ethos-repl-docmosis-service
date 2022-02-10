package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItemComparator;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;

@Service
public class BfActionReport {
    private static final String SPACE = " ";
    private static final String DATE_TIME_SEPARATOR = "T";
    private static final String MILLISECOND_PART = ".000";

    public ListingData runReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        BfActionReportData bfActionReportData = new BfActionReportData();
        if (!CollectionUtils.isEmpty(submitEvents)) {
            List<BFDateTypeItem> bfDateTypeItems = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                addBfDateTypeItems(submitEvent, listingDetails.getCaseData(), bfDateTypeItems);
            }
            bfDateTypeItems.sort(new BFDateTypeItemComparator());
            bfActionReportData.setBfDateCollection(bfDateTypeItems);
        }

        String caseTypeId = listingDetails.getCaseTypeId();
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        bfActionReportData.clearReportFields();
        bfActionReportData.setReportType(BROUGHT_FORWARD_REPORT);
        bfActionReportData.setDocumentName(BROUGHT_FORWARD_REPORT);
        bfActionReportData.setOffice(office);
        bfActionReportData.setDurationDescription(ReportHelper.getDurationText(listingDetails.getCaseData()));
        return bfActionReportData;
    }

    private static void addBfDateTypeItems(SubmitEvent submitEvent, ListingData listingData,
        List<BFDateTypeItem> bfDateTypeItems) {
        if (!CollectionUtils.isEmpty(submitEvent.getCaseData().getBfActions())) {
            for (var bfActionTypeItem : submitEvent.getCaseData().getBfActions()) {
                var bfDateTypeItem = getBFDateTypeItem(bfActionTypeItem, listingData,
                    submitEvent.getCaseData());
                if (bfDateTypeItem != null && bfDateTypeItem.getValue() != null) {
                    bfDateTypeItems.add(bfDateTypeItem);
                }
            }
        }
    }

    private static BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem,
                                                    ListingData listingData, CaseData caseData) {
        var bfActionType = bfActionTypeItem.getValue();

        if (!isNullOrEmpty(bfActionType.getBfDate()) && isNullOrEmpty(bfActionType.getCleared())) {
            var bfDate = getFormattedLocalDate(bfActionType.getBfDate());
            boolean isValidBfDate = ReportHelper.validateMatchingDate(listingData, bfDate);

            if (isValidBfDate) {
                return createBFDateTypeItem(bfActionTypeItem, bfDate, caseData.getEthosCaseReference());
            }
        }
        return null;
    }

    private static BFDateTypeItem createBFDateTypeItem(BFActionTypeItem bfActionTypeItem, String bfDate,
                                             String ethosCaseReference) {
        var bfActionType = bfActionTypeItem.getValue();
        var bfDateType = new BFDateType();
        bfDateType.setCaseReference(ethosCaseReference);

        if (!Strings.isNullOrEmpty(bfActionType.getAllActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getAllActions());
        } else if (!Strings.isNullOrEmpty(bfActionType.getCwActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getCwActions());
        }

        bfDateType.setBroughtForwardEnteredDate(bfActionType.getDateEntered());
        bfDateType.setBroughtForwardDate(bfDate);
        bfDateType.setBroughtForwardDateReason(bfActionType.getNotes());
        var bfDateTypeItem = new BFDateTypeItem();
        bfDateTypeItem.setId(String.valueOf(bfActionTypeItem.getId()));
        bfDateTypeItem.setValue(bfDateType);
        return bfDateTypeItem;
    }

    private static String getFormattedLocalDate(String bfDate) {
        if (bfDate == null || bfDate.length() < 10) {
            return null;
        }
        if (bfDate.contains(DATE_TIME_SEPARATOR) && bfDate.endsWith(MILLISECOND_PART)) {
            return LocalDateTime.parse(bfDate, OLD_DATE_TIME_PATTERN).toLocalDate().toString();
        } else if (bfDate.contains(DATE_TIME_SEPARATOR)) {
            return LocalDate.parse(bfDate.split(DATE_TIME_SEPARATOR)[0], OLD_DATE_TIME_PATTERN2).toString();
        } else if (bfDate.contains(SPACE)) {
            return LocalDate.parse(bfDate.split(SPACE)[0], OLD_DATE_TIME_PATTERN2).toString();
        }
        return LocalDate.parse(bfDate, OLD_DATE_TIME_PATTERN2).toString();
    }
}
