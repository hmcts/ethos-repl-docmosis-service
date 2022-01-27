package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import java.text.DecimalFormat;
import java.util.List;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_OFFICE;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaySummaryItem;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReportDetail;

public class BfActionReportDoc {

    public StringBuilder getReportDocPart(ListingData data) {
        return getMemberDaysReport(data);
    }

    private StringBuilder getMemberDaysReport(ListingData listingData) {
        if (!(listingData instanceof BfActionReportData)) {
            throw new IllegalStateException(("ListingData is not instance of BfActionReportData"));
        }

        var reportData = (BfActionReportData) listingData;
        var sb = new StringBuilder();
        sb.append("\"Duration_Description\":\"").append(
            nullCheck(reportData.durationDescription)).append(NEW_LINE);
        sb.append(REPORT_OFFICE).append(nullCheck(reportData.office)).append(NEW_LINE);

        sb.append("\"bf_list\":[\n");
        sb.append(addBfActionItemsList(reportData.getBfDateCollection()));
        sb.append("],\n");

        return sb;
    }

    private static StringBuilder addBfActionItemsList(List<BFDateTypeItem> bfDateTypeItems) {
        var bfActionItemsListContent = new StringBuilder();

        if (CollectionUtils.isEmpty(bfDateTypeItems)) {
            return bfActionItemsListContent;
        }

        var itemsCount = bfDateTypeItems.size();
        for (var i = 0; i < itemsCount; i++) {
            bfActionItemsListContent.append(getBfActionRow(bfDateTypeItems.get(i)));
            if ((itemsCount - i) > 1) {
                bfActionItemsListContent.append(",\n");
            }
        }

        return bfActionItemsListContent;
    }

    private static StringBuilder getBfActionRow(BFDateTypeItem bfActionItem) {
        var rowContent = new StringBuilder();
        rowContent.append("{\n\"Case_No\":\"").append(
            nullCheck(bfActionItem.getValue().getCaseReference())).append(NEW_LINE);
        rowContent.append("\"Bf_Action\":\"").append(
            nullCheck(bfActionItem.getValue().getBroughtForwardAction())).append(NEW_LINE);
        rowContent.append("\"Date_Taken\":\"").append(
            nullCheck(bfActionItem.getValue().getBroughtForwardDate())).append(NEW_LINE);
        rowContent.append("\"Bf_Date\":\"").append(
            nullCheck(bfActionItem.getValue().getBroughtForwardDate())).append(NEW_LINE);
        rowContent.append("\"Comments\":\"").append(nullCheck(bfActionItem.getValue().getBroughtForwardDateReason()))
            .append("\"\n}");

        return rowContent;
    }

}
