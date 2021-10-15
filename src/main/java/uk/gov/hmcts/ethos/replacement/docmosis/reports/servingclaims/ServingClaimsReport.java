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
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServingClaimsReport {
private static final String SERVED_REJECTED_CLAIM = "Served Rejected Claim";
    private static final String SERVED_ACCEPTED_CLAIM = "Served Accepted Claim";

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
        listingData.setLocalReportsDetailHdr(adhocReportType);
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

            var totalServedAcceptedClaims = getTotalServedAcceptedClaims(adhocReportType);

            //set day one details
            setDay1ServedClaimsDetails(adhocReportType, totalServedAcceptedClaims);

            //set day two details

            //set day three details

            //set day four details

            //set day five details

            //set day six plus details
        }
        var haha = ";";
    }

    private void populateLocalReportDetail(ListingDetails listingDetails,
                                           List<SubmitEvent> submitEvents) {
        var listingData = listingDetails.getCaseData();
        var reportsDetails = listingData.getLocalReportsDetail();
        var adhocReportType = new AdhocReportType();

        for (var submitEvent : submitEvents) {
            setLocalReportsDetail(adhocReportType, submitEvent.getCaseData());
        }

        var adhocReportTypeItem = new AdhocReportTypeItem();
        adhocReportTypeItem.setId(java.util.UUID.randomUUID().toString());
        adhocReportTypeItem.setValue(adhocReportType);
        reportsDetails.add(adhocReportTypeItem);

        listingDetails.getCaseData().setLocalReportsDetail(reportsDetails);
    }

    private void setLocalReportsDetail(AdhocReportType adhocReportType, CaseData caseData) {

        //set days to claims served
        if (!Strings.isNullOrEmpty(caseData.getReceiptDate())) {
            var caseReceiptDate = LocalDate.parse(caseData.getReceiptDate()).atStartOfDay();
            var caseClaimServedDate = LocalDate.parse(caseData.getClaimServedDate()).atStartOfDay();
            var duration = Duration.between(caseReceiptDate, caseClaimServedDate).abs();

            // set the ClaimServedTypeItem object
            var claimServedTypeItem = new ClaimServedTypeItem();
            claimServedTypeItem.setNumberOfDaysToServingClaim(String.valueOf(duration.toDays()));
            claimServedTypeItem.setCaseReceiptDate(caseReceiptDate.toString());
            claimServedTypeItem.setClaimServedDate(caseClaimServedDate.toString());
            claimServedTypeItem.setClaimServedCaseNumber(caseData.getEthosCaseReference());

            adhocReportType.getClaimServedItems().add(claimServedTypeItem);
        }

    }

    private String getTotalServedAcceptedClaims(AdhocReportType adhocReportType) {
        String totalCount = "0";

        if(adhocReportType.getClaimServedItems() != null &&
                !adhocReportType.getClaimServedItems().isEmpty()) {

            var count = adhocReportType.getClaimServedItems().stream()
                    .filter(item -> SERVED_ACCEPTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                    .collect(Collectors.toList()).size();
            totalCount = String.valueOf(count);
        }

        return totalCount;
    }

    private void setDay1ServedClaimsDetails(AdhocReportType adhocReportType,
                                            String totalServedAcceptedClaims) {
        // Set accepted claims details
        setDay1ServedAcceptedClaimsDetails(adhocReportType, totalServedAcceptedClaims);

        // Set rejected claims details
        setDay1ServedRejectedClaimsDetails(adhocReportType, totalServedAcceptedClaims);

    }

    private void setDay1ServedAcceptedClaimsDetails(AdhocReportType adhocReportType,
                                                    String totalServedAcceptedClaims) {
        // Set total of Accepted Claim Served on Day 1
        var servedAcceptedClaimItems = adhocReportType.getClaimServedItems().stream()
                .filter(item -> Integer.parseInt(item.getNumberOfDaysToServingClaim()) == 1 &&
                        SERVED_ACCEPTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                .collect(Collectors.toList());
        adhocReportType.setAcceptedClaimServedDay1Total(String.valueOf(servedAcceptedClaimItems.size()));

        // Set percentage of Accepted Claim Served on Day 1
        var percentage = (servedAcceptedClaimItems.size()/Integer.parseInt(totalServedAcceptedClaims)) * 100;
        adhocReportType.setAcceptedClaimServedDay1Percent(String.valueOf(percentage));
    }
    private void setDay1ServedRejectedClaimsDetails(AdhocReportType adhocReportType,
                                                    String totalServedAcceptedClaims) {
        // Set total of Rejected Claim Served on Day 1
        var servedRejectedClaimItems = adhocReportType.getClaimServedItems().stream()
                .filter(item -> Integer.parseInt(item.getNumberOfDaysToServingClaim()) == 1 &&
                        SERVED_REJECTED_CLAIM.equalsIgnoreCase(item.getClaimServedType()))
                .collect(Collectors.toList());
        adhocReportType.setRejectedClaimServedDay1Total(String.valueOf(servedRejectedClaimItems.size()));

        // Set percentage of Rejected Claim Served on Day 1
        var percentage = (servedRejectedClaimItems.size()/Integer.parseInt(totalServedAcceptedClaims)) * 100;
        adhocReportType.setAcceptedClaimServedDay1Percent(String.valueOf(percentage));
    }

}
