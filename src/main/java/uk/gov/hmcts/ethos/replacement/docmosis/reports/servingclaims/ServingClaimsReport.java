package uk.gov.hmcts.ethos.replacement.docmosis.reports.servingclaims;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.listing.types.ClaimServedTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import org.elasticsearch.common.Strings;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.text.DecimalFormat;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import uk.gov.hmcts.ecm.common.model.listing.types.ClaimServedType;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import java.time.Period;
import java.time.temporal.ChronoUnit;
@Service
@Slf4j
public class ServingClaimsReport {

    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        initReport(listingDetails);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        return listingDetails.getCaseData();
    }

    private void initReport(ListingDetails listingDetails) {

        var listingData = listingDetails.getCaseData();
        var adhocReportType = new AdhocReportType();
        adhocReportType.setReportOffice(UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()));
        adhocReportType.setClaimServedItems(new ArrayList<>());
        listingData.setLocalReportsDetailHdr(adhocReportType);
        listingData.setLocalReportsDetail(new ArrayList<>());
    }

    private void executeReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        //report detail
        populateLocalReportDetail(listingDetails, submitEvents);

        //report summary
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents);
    }

    private void populateLocalReportSummary(ListingData caseData, List<SubmitEvent> submitEvents){

        var adhocReportTypeItemsList = caseData.getLocalReportsDetail();

        if (adhocReportTypeItemsList != null && !adhocReportTypeItemsList.isEmpty()) {
            var adhocReportType = adhocReportTypeItemsList.get(0).getValue();

            // Duration in days between case receipt and serving claim
            int claimServedOnDayOne = 0; // served on same day
            setServedClaimsDetailsByDay(adhocReportType, claimServedOnDayOne);
            int claimServedOnDayTwo = 1; // 1 day duration
            setServedClaimsDetailsByDay(adhocReportType, claimServedOnDayTwo);
            int claimServedOnDayThree = 2;
            setServedClaimsDetailsByDay(adhocReportType, claimServedOnDayThree);
            int claimServedOnDayFour = 3;
            setServedClaimsDetailsByDay(adhocReportType, claimServedOnDayFour);
            int claimServedOnDayFive = 4;
            setServedClaimsDetailsByDay(adhocReportType, claimServedOnDayFive);
            int claimServedOnDaySixOrMore = 5;
            setServedClaimsDetailsByDay(adhocReportType, claimServedOnDaySixOrMore);
        }
    }

    private void populateLocalReportDetail(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        var listingData = listingDetails.getCaseData();
        var reportsDetails = listingData.getLocalReportsDetail();

        var adhocReportTypeItem = new AdhocReportTypeItem();
        var adhocReportType = new AdhocReportType();
            adhocReportType.setClaimServedItems(new ArrayList<>());

        for (var submitEvent : submitEvents) {
            setLocalReportsDetail(adhocReportType, submitEvent.getCaseData());
        }

        adhocReportTypeItem.setId(java.util.UUID.randomUUID().toString());
        adhocReportTypeItem.setValue(adhocReportType);

        var servedClaimItemsCount = adhocReportType.getClaimServedItems().size();
        adhocReportType.setClaimServedTotal(String.valueOf(servedClaimItemsCount));

        reportsDetails.add(adhocReportTypeItem);

        listingDetails.getCaseData().setLocalReportsDetail(reportsDetails);
    }

    private void setLocalReportsDetail(AdhocReportType adhocReportType, CaseData caseData) {

        if (!Strings.isNullOrEmpty(caseData.getReceiptDate()) &&
            !Strings.isNullOrEmpty(caseData.getClaimServedDate())) {

            LocalDate caseReceiptDate = LocalDate.parse(caseData.getReceiptDate(), OLD_DATE_TIME_PATTERN2);
            LocalDate caseClaimServedDate = LocalDate.parse(caseData.getClaimServedDate(), OLD_DATE_TIME_PATTERN2);
            long actualNumberOfDaysToServingClaim = ChronoUnit.DAYS.between(caseReceiptDate, caseClaimServedDate);
            var reportedNumberOfDaysToServingClaim = getReportedNumberOfDays(caseReceiptDate, caseClaimServedDate);

            var claimServedType = new ClaimServedType();
            claimServedType.setReportedNumberOfDays(String.valueOf(reportedNumberOfDaysToServingClaim));
            claimServedType.setActualNumberOfDays(String.valueOf(actualNumberOfDaysToServingClaim));
            claimServedType.setCaseReceiptDate(caseReceiptDate.toString());
            claimServedType.setClaimServedDate(caseClaimServedDate.toString());
            claimServedType.setClaimServedCaseNumber(caseData.getEthosCaseReference());
            claimServedType.setClaimServedType(getServedClaimStatus(caseData));

            var claimServedTypeItem = new ClaimServedTypeItem();
            claimServedTypeItem.setValue(claimServedType);

            adhocReportType.getClaimServedItems().add(claimServedTypeItem);
        }
    }

    private String getServedClaimStatus(CaseData caseData) {
        String servedClaimStatus = null;
        var casePreAcceptType = caseData.getPreAcceptCase();

        if(casePreAcceptType != null) {
            servedClaimStatus = casePreAcceptType.getCaseAccepted();
        }

        return servedClaimStatus;
    }

    private int getReportedNumberOfDays(LocalDate caseReceiptDate, LocalDate caseClaimServedDate) {
        Period period = Period.between(caseReceiptDate, caseClaimServedDate);
        int totalNumberOfDays;

        if (period.getMonths() > 0 || period.getDays() >= 5) {
            totalNumberOfDays = 5;
        } else {
            totalNumberOfDays = period.getDays();
        }

        return totalNumberOfDays;
    }

    private String getTotalServedClaims(AdhocReportType adhocReportType) {
        String totalCount = "0";

        if(adhocReportType.getClaimServedItems() != null &&
           !adhocReportType.getClaimServedItems().isEmpty()) {
            totalCount = String.valueOf(adhocReportType.getClaimServedItems().size());
        }

        return totalCount;
    }


    private void setServedClaimsDetailsByDay(AdhocReportType adhocReportType, int dayNumber) {
        var totalServedClaims = getTotalServedClaims(adhocReportType);
        setServedClaimsSummary(adhocReportType, totalServedClaims, dayNumber);
    }

    private void setServedClaimsSummary(AdhocReportType adhocReportType,
                                          String totalServedClaims,
                                          int dayNumber) {
        int claimsServedDayListUpperBoundary = 5;
        List<ClaimServedTypeItem> acceptedClaimItems;

        if(dayNumber >= claimsServedDayListUpperBoundary) {
            acceptedClaimItems = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> Integer.parseInt(item.getValue().getReportedNumberOfDays()) >= dayNumber &&
                            !REJECTED_STATE.equalsIgnoreCase(item.getValue().getClaimServedType()))
                    .collect(Collectors.toList());
        }
        else {
            acceptedClaimItems = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> Integer.parseInt(item.getValue().getReportedNumberOfDays()) == dayNumber &&
                            !REJECTED_STATE.equalsIgnoreCase(item.getValue().getClaimServedType()))
                    .collect(Collectors.toList());
        }

        var acceptedClaimItemsCount = String.valueOf(acceptedClaimItems.size());

        var percentage = "0";
        if (Integer.parseInt(totalServedClaims) > 0) {
            var decimalFormatter = new DecimalFormat("#.##");
            var result = 100.0 * (acceptedClaimItems.size() / Double.parseDouble(totalServedClaims));
            percentage = String.valueOf(decimalFormatter.format(result));
        }

        switch (dayNumber) {
            case 0:
                adhocReportType.setClaimServedDay1Total(acceptedClaimItemsCount);
                adhocReportType.setClaimServedDay1Percent(percentage);
                break;
            case 1:
                adhocReportType.setClaimServedDay2Total(acceptedClaimItemsCount);
                adhocReportType.setClaimServedDay2Percent(percentage);
                break;
            case 2:
                adhocReportType.setClaimServedDay3Total(acceptedClaimItemsCount);
                adhocReportType.setClaimServedDay3Percent(percentage);
                break;
            case 3:
                adhocReportType.setClaimServedDay4Total(acceptedClaimItemsCount);
                adhocReportType.setClaimServedDay4Percent(percentage);
                break;
            case 4:
                adhocReportType.setClaimServedDay5Total(acceptedClaimItemsCount);
                adhocReportType.setClaimServedDay5Percent(percentage);
                break;
            default:
                adhocReportType.setClaimServed6PlusDaysTotal(acceptedClaimItemsCount);
                adhocReportType.setClaimServed6PlusDaysPercent(percentage);
                break;
        }
    }

}
