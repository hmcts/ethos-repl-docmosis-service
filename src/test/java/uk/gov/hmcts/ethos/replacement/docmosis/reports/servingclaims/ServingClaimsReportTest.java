package uk.gov.hmcts.ethos.replacement.docmosis.reports.servingclaims;

import org.assertj.core.util.Strings;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;

public class ServingClaimsReportTest {

    private List<SubmitEvent> submitEvents;
    private ListingDetails listingDetails;

    @Before
    public void setUp() {
        listingDetails = new ListingDetails();
        var listingDataRange = new ListingData();
        listingDataRange.setListingDateFrom("2020-08-02");
        listingDataRange.setListingDateTo("2020-08-24");
        listingDataRange.setListingVenue("Leeds");
        listingDataRange.setReportType("Claims Served");
        listingDetails.setCaseData(listingDataRange);
        listingDetails.setCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID);
        listingDetails.setJurisdiction("EMPLOYMENT");

        var submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseId(1);
        submitEvent1.setState(ACCEPTED_STATE);

        var caseData = new CaseData();
        caseData.setEthosCaseReference("1800522/2020");
        caseData.setReceiptDate("2020-08-10");
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setDateAccepted("2020-08-10");
        caseData.setPreAcceptCase(casePreAcceptType);
        caseData.setEcmCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem = new BFActionTypeItem();
        var bfActionType = new BFActionType();
        bfActionType.setBfDate("2020-08-10");
        bfActionType.setNotes("Test Notes One");
        bfActionTypeItem.setId("0011");
        bfActionTypeItem.setValue(bfActionType);
        caseData.setBfActions(List.of(bfActionTypeItem));
        caseData.setClaimServedDate("2020-08-10");
        submitEvent1.setCaseData(caseData);

        var submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseId(2);
        submitEvent2.setState(ACCEPTED_STATE);

        var caseData2 = new CaseData();
        caseData2.setEthosCaseReference("1800523/2020");
        caseData2.setReceiptDate("2020-08-15");
        var casePreAcceptType2 = new CasePreAcceptType();
        casePreAcceptType2.setDateAccepted("2020-08-16");
        caseData2.setPreAcceptCase(casePreAcceptType2);
        caseData2.setEcmCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem2 = new BFActionTypeItem();
        var bfActionType2 = new BFActionType();
        bfActionType2.setBfDate("2020-08-18");
        bfActionType2.setNotes("Test Notes Two");
        bfActionTypeItem2.setId("0012");
        bfActionTypeItem2.setValue(bfActionType2);
        caseData2.setBfActions(List.of(bfActionTypeItem2));
        caseData2.setClaimServedDate("2020-08-18");
        submitEvent2.setCaseData(caseData2);

        var submitEvent3 = new SubmitEvent();
        submitEvent3.setCaseId(3);
        submitEvent3.setState(TRANSFERRED_STATE);

        var caseData3 = new CaseData();
        caseData3.setEthosCaseReference("1800524/2020");
        caseData3.setReceiptDate("2020-08-25");
        var casePreAcceptType3 = new CasePreAcceptType();
        casePreAcceptType3.setDateAccepted("2020-08-25");
        caseData3.setPreAcceptCase(casePreAcceptType3);
        caseData3.setEcmCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem3 = new BFActionTypeItem();
        var bfActionType3 = new BFActionType();
        bfActionType3.setBfDate("2020-08-25");
        bfActionType3.setNotes("Test Notes Three");
        bfActionTypeItem3.setId("0013");
        bfActionTypeItem3.setValue(bfActionType3);
        caseData3.setBfActions(List.of(bfActionTypeItem3));
        caseData3.setClaimServedDate("2020-08-25");
        submitEvent3.setCaseData(caseData3);

        var submitEvent4 = new SubmitEvent();
        submitEvent4.setCaseId(4);
        submitEvent4.setState(CLOSED_STATE);

        var caseData4 = new CaseData();
        caseData4.setEthosCaseReference("1800525/2020");
        caseData4.setReceiptDate("2020-04-10");
        var casePreAcceptType4 = new CasePreAcceptType();
        casePreAcceptType4.setDateAccepted("2020-08-07");
        caseData4.setPreAcceptCase(casePreAcceptType4);
        caseData4.setEcmCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem4 = new BFActionTypeItem();
        var bfActionType4 = new BFActionType();
        bfActionType4.setBfDate("2020-08-10");
        bfActionType4.setNotes("Test Notes Four");
        bfActionTypeItem4.setId("0014");
        bfActionTypeItem4.setValue(bfActionType4);
        caseData4.setBfActions(List.of(bfActionTypeItem4));
        caseData4.setClaimServedDate("2020-08-15");
        submitEvent4.setCaseData(caseData4);

        var submitEvent5 = new SubmitEvent();
        submitEvent5.setCaseId(5);
        submitEvent5.setState(ACCEPTED_STATE);

        var caseData5 = new CaseData();
        caseData5.setEthosCaseReference("1800528/2020");
        caseData5.setReceiptDate("2020-08-19");
        var casePreAcceptType5 = new CasePreAcceptType();
        casePreAcceptType5.setDateAccepted("2020-08-07");
        caseData5.setPreAcceptCase(casePreAcceptType5);
        caseData5.setEcmCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem5 = new BFActionTypeItem();
        var bfActionType5 = new BFActionType();
        bfActionType5.setBfDate("2020-08-19");
        bfActionType5.setNotes("Test Notes Five");
        bfActionTypeItem5.setId("0014");
        bfActionTypeItem5.setValue(bfActionType5);
        caseData5.setBfActions(List.of(bfActionTypeItem5));
        caseData5.setClaimServedDate("2020-08-21");
        submitEvent5.setCaseData(caseData5);

        submitEvents = new ArrayList<>();
        submitEvents.add(submitEvent1);
        submitEvents.add(submitEvent2);
        submitEvents.add(submitEvent3);
        submitEvents.add(submitEvent4);
        submitEvents.add(submitEvent5);
    }

    @Test
    public void shouldIncludeCasesWithClaimsServedDate() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var caseTotalCount =  resultListingData.getLocalReportsDetail().getFirst()
                .getValue().getClaimServedTotal();
        var actualCount = Strings.isNullOrEmpty(caseTotalCount)  ? 0 : Integer.parseInt(caseTotalCount);
        assertEquals(5, actualCount);
    }

    @Test
    public void shouldReturnCorrectCasesCountByServingDay() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var adhocReportType =  resultListingData.getLocalReportsDetail().getFirst()
                .getValue();
        var claimServedItems = adhocReportType.getClaimServedItems();
        var counts = new long[6];
        claimServedItems.stream()
            .mapToInt(item -> Integer.parseInt(item.getValue().getReportedNumberOfDays()))
            .forEach(days -> {
                if (days >= 5) {
                    counts[5]++;
                } else {
                    counts[days]++;
                }
            });
        assertEquals(2, counts[0]);
        assertEquals(1, counts[1]);
        assertEquals(1, counts[2]);
        assertEquals(0, counts[3]);
        assertEquals(0, counts[4]);
        assertEquals(1, counts[5]);

    }

    @Test
    public void shouldSetCorrectCountForDay1Serving() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var adhocReportType =  resultListingData.getLocalReportsDetail().getFirst()
                .getValue();
        var expectedDay1Count = adhocReportType.getClaimServedDay1Total();
        var expectedDay1Percent = adhocReportType.getClaimServedDay1Percent();
        assertEquals("2", expectedDay1Count);
        assertEquals("40", expectedDay1Percent);
    }

    @Test
    public void shouldSetCorrectCountForDay2Serving() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var adhocReportType =  resultListingData.getLocalReportsDetail().getFirst()
                .getValue();
        var expectedDay2Count = adhocReportType.getClaimServedDay2Total();
        var expectedDay2Percent = adhocReportType.getClaimServedDay2Percent();
        assertEquals("1", expectedDay2Count);
        assertEquals("20", expectedDay2Percent);
    }

    @Test
    public void shouldSetCorrectCountForDay3Serving() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var adhocReportType =  resultListingData.getLocalReportsDetail().getFirst()
                .getValue();
        var expectedDay3Count = adhocReportType.getClaimServedDay3Total();
        var expectedDay3Percent = adhocReportType.getClaimServedDay3Percent();
        assertEquals("1", expectedDay3Count);
        assertEquals("20", expectedDay3Percent);
    }

    @Test
    public void shouldSetCorrectCountFor6PlusDaysServing() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var adhocReportType =  resultListingData.getLocalReportsDetail().getFirst()
                .getValue();
        var expectedDay6PlusDaysCount = adhocReportType.getClaimServed6PlusDaysTotal();
        var expectedDay6PlusDaysPercent = adhocReportType.getClaimServed6PlusDaysPercent();
        assertEquals("1", expectedDay6PlusDaysCount);
        assertEquals("20", expectedDay6PlusDaysPercent);
    }

    @Test
    public void shouldSetCorrectActualAndReportedDayCountFor6PlusDaysServing() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var claimServedItems = resultListingData.getLocalReportsDetail()
                .getFirst().getValue().getClaimServedItems();
        var expectedDay6PlusItems = claimServedItems.stream()
            .filter(x -> Integer.parseInt(x.getValue().getReportedNumberOfDays()) >= 5)
                .toList();
        var firstClaimServedItem = expectedDay6PlusItems.getFirst();

        var reportedNumberOfDays = firstClaimServedItem.getValue().getReportedNumberOfDays();
        var actualNumberOfDays = firstClaimServedItem.getValue().getActualNumberOfDays();
        assertEquals("5", reportedNumberOfDays);
        assertEquals("92", actualNumberOfDays);
    }

    @Test
    public void shouldSetCorrectDayForLessThan6DaysServingClaim() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var claimServedItems = resultListingData.getLocalReportsDetail()
                .getFirst().getValue().getClaimServedItems();
        var secondClaimServedItem = claimServedItems.get(1);
        var numberOfDays = secondClaimServedItem.getValue().getActualNumberOfDays();
        assertEquals("2", numberOfDays);
    }

    @Test
    public void shouldNotAddServedClaimItemWhenNoClaimsServedFound() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, null);
        var claimServedItemsCount = resultListingData.getLocalReportsDetail().getFirst().getValue()
                .getClaimServedItems().size();
        assertEquals("0", String.valueOf(claimServedItemsCount));
    }

    @Test
    public void shouldNotIncludeCasesWithNoReceiptDateAndClaimServedDateProvided() {
        var servingClaimsReport = new ServingClaimsReport();
        var caseOne = submitEvents.getFirst();
        caseOne.getCaseData().setReceiptDate(null);
        caseOne.getCaseData().setClaimServedDate(null);
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var claimServedItems = resultListingData.getLocalReportsDetail()
                .getFirst().getValue().getClaimServedItems();
        var itemsListDoesNotContainCaseOneEntry = claimServedItems.stream()
                .filter(x -> x.getValue().getClaimServedCaseNumber()
                    .equals(caseOne.getCaseData().getEthosCaseReference())).count();
        assertEquals(4, claimServedItems.size());
        assertEquals(0, itemsListDoesNotContainCaseOneEntry);
    }

    @Test
    public void shouldSetCorrectReportedNumberOfDays() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var fourthClaimServedItem = resultListingData.getLocalReportsDetail().getFirst().getValue()
                .getClaimServedItems().get(3);
        assertEquals("5", fourthClaimServedItem.getValue().getReportedNumberOfDays());
    }

    @Test
    public void shouldSetReportSummaryFromReportDetailsWhenReportDetailsIsNotEmpty() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var localReportsDetailSize = resultListingData.getLocalReportsDetail().size();
        var claimServedItemsCount = resultListingData.getLocalReportsDetail().getFirst().getValue()
                .getClaimServedItems().size();

        assertEquals(1, localReportsDetailSize);
        assertEquals(5, claimServedItemsCount);
    }

    @Test
    public void shouldNotSetReportSummaryFromReportDetailsWhenReportDetailsIsEmpty() {
        var servingClaimsReport = new ServingClaimsReport();
        //Set ReceiptDate for each case to null to make LocalReportsDetail empty
        submitEvents.forEach(s -> s.getCaseData().setReceiptDate(null));
        var resultListingData = servingClaimsReport.generateReportData(listingDetails, submitEvents);
        var localReportsDetailCount = resultListingData.getLocalReportsDetail().size();
        var claimServedItemsCount = resultListingData.getLocalReportsDetail().getFirst().getValue()
                .getClaimServedItems().size();
        assertEquals(1, localReportsDetailCount);
        assertEquals(0, claimServedItemsCount);
    }
}