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

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

import java.time.Period;

@Service
@Slf4j
public class ServingClaimsReport {
private static final String REJECTED_CLAIM = "Rejected Claim";
    private static final String ACCEPTED_CLAIM = "Accepted Claim";

    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        initReport(listingDetails);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        return new ListingData();
    }

    private void initReport(ListingDetails listingDetails) {

        var listingData = listingDetails.getCaseData();
        var adhocReportType = new AdhocReportType();
        adhocReportType.setReportOffice(listingData.getListingVenue());
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

            setServedClaimsDetailsByDay(adhocReportType, 1);

            setServedClaimsDetailsByDay(adhocReportType, 2);

            setServedClaimsDetailsByDay(adhocReportType, 3);

            setServedClaimsDetailsByDay(adhocReportType, 4);

            setServedClaimsDetailsByDay(adhocReportType, 5);

            setServedClaimsDetailsByDay(adhocReportType, 6);
        }
    }

    private void populateLocalReportDetail(ListingDetails listingDetails,
                                           List<SubmitEvent> submitEvents) {
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

        reportsDetails.add(adhocReportTypeItem);

        listingDetails.getCaseData().setLocalReportsDetail(reportsDetails);
    }

    private void setLocalReportsDetail(AdhocReportType adhocReportType, CaseData caseData) {

        //set days to claims served
        if (!Strings.isNullOrEmpty(caseData.getReceiptDate()) &&
            !Strings.isNullOrEmpty(caseData.getClaimServedDate())) {

            LocalDate caseReceiptDate = LocalDate.parse(caseData.getReceiptDate(), OLD_DATE_TIME_PATTERN2);
            LocalDate caseClaimServedDate = LocalDate.parse(caseData.getClaimServedDate(), OLD_DATE_TIME_PATTERN2);
            Period period = Period.between(caseReceiptDate, caseClaimServedDate);

            var claimServedTypeItem = new ClaimServedTypeItem();
            var numberOfDaysToServingClaim = getNumberOfDays(caseReceiptDate, caseClaimServedDate);
            claimServedTypeItem.setNumberOfDaysToServingClaim(String.valueOf(numberOfDaysToServingClaim));
            claimServedTypeItem.setCaseReceiptDate(caseReceiptDate.toString());
            claimServedTypeItem.setClaimServedDate(caseClaimServedDate.toString());
            claimServedTypeItem.setClaimServedCaseNumber(caseData.getEthosCaseReference());
            claimServedTypeItem.setClaimServedType(getServedClaimStatus(caseData));

            adhocReportType.getClaimServedItems().add(claimServedTypeItem);
        }

    }

    private String getServedClaimStatus(CaseData caseData) {
        String servedClaimStatus = null;
        var casePreAcceptType = caseData.getPreAcceptCase();

        if(casePreAcceptType != null) {
            var status = casePreAcceptType.getCaseAccepted();
            if(YES.equals(status) && casePreAcceptType.getDateAccepted() != null) {
                servedClaimStatus = ACCEPTED_CLAIM;
            }
            if(NO.equals(status) && casePreAcceptType.getDateRejected() != null) {
                servedClaimStatus = REJECTED_CLAIM;
            }
        }

        return servedClaimStatus;
    }

    private int getNumberOfDays(LocalDate caseReceiptDate, LocalDate caseClaimServedDate) {
        Period period = Period.between(caseReceiptDate, caseClaimServedDate);
        int totalNumberOfDays;

        if (period.getMonths() > 0 || period.getDays() >= 6) {
            totalNumberOfDays = 6;
        } else {
            totalNumberOfDays = period.getDays();
        }

        return totalNumberOfDays;
    }

    private String getTotalServedAcceptedClaims(AdhocReportType adhocReportType) {
        String totalCount = "0";

        if(adhocReportType.getClaimServedItems() != null &&
                !adhocReportType.getClaimServedItems().isEmpty()) {

            var count = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> ACCEPTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                    .collect(Collectors.toList()).size();
            totalCount = String.valueOf(count);
        }

        return totalCount;
    }

    private String getTotalRejectedClaims(AdhocReportType adhocReportType) {
        String totalCount = "0";

        if(adhocReportType.getClaimServedItems() != null &&
                !adhocReportType.getClaimServedItems().isEmpty()) {

            var count = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> REJECTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                    .collect(Collectors.toList()).size();

            totalCount = String.valueOf(count);
        }

        return totalCount;
    }

    private void setServedClaimsDetailsByDay(AdhocReportType adhocReportType, int dayNumber) {
        var totalAcceptedClaims = getTotalServedAcceptedClaims(adhocReportType);
        var totalRejectedClaims = getTotalRejectedClaims(adhocReportType);

        setAcceptedClaimsSummary(adhocReportType, totalAcceptedClaims, dayNumber);

        setRejectedClaimsSummary(adhocReportType, totalRejectedClaims, dayNumber);
    }

    private void setAcceptedClaimsSummary(AdhocReportType adhocReportType,
                                          String totalAcceptedClaims,
                                          int dayNumber) {
        List<ClaimServedTypeItem> acceptedClaimItems = new ArrayList<>();
        if(dayNumber >= 6) {
            acceptedClaimItems = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> Integer.parseInt(item.getNumberOfDaysToServingClaim()) >= dayNumber &&
                            ACCEPTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                    .collect(Collectors.toList());
        }
        else {
            acceptedClaimItems = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> Integer.parseInt(item.getNumberOfDaysToServingClaim()) == dayNumber &&
                            ACCEPTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                    .collect(Collectors.toList());
        }

        var acceptedClaimItemsCount = String.valueOf(acceptedClaimItems.size());

        var percentage = "0";
        if (Integer.parseInt(totalAcceptedClaims) > 0) {
            percentage = String.valueOf((acceptedClaimItems.size()/Integer.parseInt(totalAcceptedClaims)) * 100);
        }

        switch (dayNumber) {
            case 1:
                adhocReportType.setAcceptedClaimServedDay1Total(acceptedClaimItemsCount);
                adhocReportType.setAcceptedClaimServedDay1Percent(percentage);
                break;
            case 2:
                adhocReportType.setAcceptedClaimServedDay2Total(acceptedClaimItemsCount);
                adhocReportType.setAcceptedClaimServedDay2Percent(percentage);
                break;
            case 3:
                adhocReportType.setAcceptedClaimServedDay3Total(acceptedClaimItemsCount);
                adhocReportType.setAcceptedClaimServedDay3Percent(percentage);
                break;
            case 4:
                adhocReportType.setAcceptedClaimServedDay4Total(acceptedClaimItemsCount);
                adhocReportType.setAcceptedClaimServedDay4Percent(percentage);
                break;
            case 5:
                adhocReportType.setAcceptedClaimServedDay5Total(acceptedClaimItemsCount);
                adhocReportType.setAcceptedClaimServedDay5Percent(percentage);
                break;
            default:
                adhocReportType.setAcceptedClaimServed6PlusDaysTotal(acceptedClaimItemsCount);
                adhocReportType.setAcceptedClaimServed6PlusDaysPercent(percentage);
                break;
        }
    }

    private void setRejectedClaimsSummary(AdhocReportType adhocReportType,
                                          String totalRejectedClaims,
                                          int dayNumber) {
        var percentage = "0";
        // Set rejected claims details
        var rejectedClaimItems = adhocReportType.getClaimServedItems().stream()
                .filter(item -> Integer.parseInt(item.getNumberOfDaysToServingClaim()) == dayNumber &&
                        REJECTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                .collect(Collectors.toList());

        var rejectedClaimItemsCount = String.valueOf(rejectedClaimItems.size());
        if (Integer.parseInt(totalRejectedClaims) > 0) {
            percentage = String.valueOf((rejectedClaimItems.size()/Integer.parseInt(totalRejectedClaims)) * 100);
        }

        switch (dayNumber){
            case 1:
                adhocReportType.setRejectedClaimServedDay1Total(rejectedClaimItemsCount);
                adhocReportType.setRejectedClaimServedDay1Percent(percentage);
                break;
            case 2:
                adhocReportType.setAcceptedClaimServedDay2Total(rejectedClaimItemsCount);
                adhocReportType.setRejectedClaimServedDay2Percent(percentage);
                break;
            case 3:
                adhocReportType.setAcceptedClaimServedDay3Total(rejectedClaimItemsCount);
                adhocReportType.setRejectedClaimServedDay3Percent(percentage);
                break;
            case 4:
                adhocReportType.setAcceptedClaimServedDay4Total(rejectedClaimItemsCount);
                adhocReportType.setRejectedClaimServedDay4Percent(percentage);
                break;
            case 5:
                adhocReportType.setAcceptedClaimServedDay5Total(rejectedClaimItemsCount);
                adhocReportType.setRejectedClaimServedDay5Percent(percentage);
                break;
            default:
                adhocReportType.setAcceptedClaimServed6PlusDaysTotal(rejectedClaimItemsCount);
                adhocReportType.setRejectedClaimServed6PlusDaysPercent(percentage);
                break;
        }
    }

}
