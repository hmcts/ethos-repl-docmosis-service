package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

public class HearingsByHearingTypeReport {

    public ListingData processHearingsByHearingTypeRequest(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        initReport(listingDetails);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        listingDetails.getCaseData().clearReportFields();
        return listingDetails.getCaseData();
    }

    private void executeReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        log.info(String.format("Hearings by hearing type report case type id %s search results: %d",
                listingDetails.getCaseTypeId(), submitEvents.size()));
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummaryHdr(listingDetails);
        populateLocalReportSummaryDetail(listingDetails, submitEvents);

    }

    private void populateLocalReportSummaryHdr(ListingDetails listingDetails) {

        ListingData listingData = listingDetails.getCaseData();
        var adhocReportType = listingData.getLocalReportsSummary().get(0).getValue();
        adhocReportType.setReportOffice(UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()));
        int totalCases = Integer.parseInt(adhocReportType.getConOpenTotal())
                + Integer.parseInt(adhocReportType.getConStdTotal())
                + Integer.parseInt(adhocReportType.getConFastTotal())
                + Integer.parseInt(adhocReportType.getConNoneTotal());

        int totalCasesWithin26Weeks = Integer.parseInt(adhocReportType.getConOpen26wkTotal())
                + Integer.parseInt(adhocReportType.getConStd26wkTotal())
                + Integer.parseInt(adhocReportType.getConFast26wkTotal())
                + Integer.parseInt(adhocReportType.getConNone26wkTotal());

        int totalCasesNotWithin26Weeks = Integer.parseInt(adhocReportType.getXConOpen26wkTotal())
                + Integer.parseInt(adhocReportType.getXConStd26wkTotal())
                + Integer.parseInt(adhocReportType.getXConFast26wkTotal())
                + Integer.parseInt(adhocReportType.getXConNone26wkTotal());

        float totalCasesWithin26WeeksPercent = (totalCases != 0)
                ? ((float)totalCasesWithin26Weeks / totalCases) * 100 : 0;
        float totalCasesNotWithin26WeeksPercent = (totalCases != 0)
                ? ((float)totalCasesNotWithin26Weeks / totalCases) * 100 : 0;

        adhocReportType.setTotalCases(String.valueOf(totalCases));
        adhocReportType.setTotal26wk(String.valueOf(totalCasesWithin26Weeks));
        adhocReportType.setTotalx26wk(String.valueOf(totalCasesNotWithin26Weeks));
        adhocReportType.setTotal26wkPerCent(String.format("%.2f", totalCasesWithin26WeeksPercent));
        adhocReportType.setTotalx26wkPerCent(String.format("%.2f", totalCasesNotWithin26WeeksPercent));
        listingData.setLocalReportsDetailHdr(adhocReportType);
    }

    private LocalDate getLocalReportsSummaryHdrFields(CaseData caseData) {
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            return null;
        }
        AdhocReportType a = null;
        for (var hearingTypeItem : caseData.getHearingCollection()) {
            if (HEARING_TYPE_JUDICIAL_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
                a.setHearing();
            }
            datesList = getHearingDateList(hearingTypeItem);
            if (CollectionUtils.isNotEmpty(datesList)) {
                mainDatesList.addAll(datesList);
            }
        }
        if (CollectionUtils.isNotEmpty(mainDatesList)) {
            Collections.sort(mainDatesList);
            return mainDatesList.get(0);
        }
        return null;
    }

    private List<LocalDate> getHearingDateList(HearingTypeItem hearingTypeItem) {
        var hearingType = hearingTypeItem.getValue();
        List<LocalDate> datesList = new ArrayList<>();
        if (hearingType == null || CollectionUtils.isEmpty(hearingType.getHearingDateCollection())) {
            return datesList;
        }
        if (Constants.HEARING_TYPE_JUDICIAL_HEARING.equals(hearingType.getHearingType())
                || HEARING_TYPE_PERLIMINARY_HEARING.equals(hearingType.getHearingType())) {
            for (var dateListedItemType : hearingType.getHearingDateCollection()) {
                if (Constants.HEARING_STATUS_HEARD.equals(dateListedItemType.getValue().getHearingStatus())) {
                    var date = LocalDate.parse(dateListedItemType.getValue().getListedDate(),  OLD_DATE_TIME_PATTERN);
                    datesList.add(date);
                }
            }
        }
        return datesList;
    }

}
