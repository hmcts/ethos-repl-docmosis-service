package uk.gov.hmcts.ethos.replacement.docmosis.reports.servingclaims;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import org.junit.Before;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import java.util.List;
import org.assertj.core.util.Strings;

public class ServingClaimsReportTest {

    private List<SubmitEvent> submitEvents;
    private ListingDetails listingDetailsSingle;
    private ListingDetails listingDetailsRange;

    @Before
    public void setUp() {

        listingDetailsRange = new ListingDetails();
        var listingDataRange = new ListingData();
        listingDataRange.setListingDateFrom("2020-08-02");
        listingDataRange.setListingDateTo("2020-08-24");
        listingDataRange.setListingVenue("Leeds");
        listingDataRange.setReportType("Claims Served");
        listingDetailsRange.setCaseData(listingDataRange);
        listingDetailsRange.setCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID);
        listingDetailsRange.setJurisdiction("EMPLOYMENT");

        listingDetailsSingle = new ListingDetails();
        var listingDataSingle = new ListingData();
        //listingDataSingle.setListingDateFrom("2020-08-02");
        //listingDataSingle.setListingDateTo("2020-08-02");
        listingDataSingle.setListingDate("2020-08-18");
        listingDataSingle.setListingVenue("Leeds");
        listingDataSingle.setReportType("Claims Served");
        listingDetailsSingle.setCaseData(listingDataSingle);
        listingDetailsSingle.setCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID);
        listingDetailsSingle.setJurisdiction("EMPLOYMENT");

        var submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseId(1);
        submitEvent1.setState(ACCEPTED_STATE);

        var caseData = new CaseData();
        caseData.setEthosCaseReference("1800522/2020");
        caseData.setReceiptDate("2020-08-10");
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setDateAccepted("2020-08-10");
        caseData.setPreAcceptCase(casePreAcceptType);
        caseData.setCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem = new BFActionTypeItem();
        var bfActionType = new BFActionType();
        bfActionType.setBfDate("2020-08-10");
        bfActionType.setNotes("Test Notes One");
        bfActionTypeItem.setId("0011");
        bfActionTypeItem.setValue(bfActionType);
        caseData.setBfActions(new ArrayList<>(Arrays.asList(bfActionTypeItem)));
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
        caseData2.setCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem2 = new BFActionTypeItem();
        var bfActionType2 = new BFActionType();
        bfActionType2.setBfDate("2020-08-18");
        bfActionType2.setNotes("Test Notes Two");
        bfActionTypeItem2.setId("0012");
        bfActionTypeItem2.setValue(bfActionType2);
        caseData2.setBfActions(new ArrayList<>(Arrays.asList(bfActionTypeItem2)));
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
        caseData3.setCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem3 = new BFActionTypeItem();
        var bfActionType3 = new BFActionType();
        bfActionType3.setBfDate("2020-08-25");
        bfActionType3.setNotes("Test Notes Three");
        bfActionTypeItem3.setId("0013");
        bfActionTypeItem3.setValue(bfActionType3);
        caseData3.setBfActions(new ArrayList<>(Arrays.asList(bfActionTypeItem3)));
        caseData3.setClaimServedDate("2020-08-25");
        submitEvent3.setCaseData(caseData3);

        var submitEvent4 = new SubmitEvent();
        submitEvent4.setCaseId(4);
        submitEvent4.setState(CLOSED_STATE);

        var caseData4 = new CaseData();
        caseData4.setEthosCaseReference("1800525/2020");
        caseData4.setReceiptDate("2020-08-10");
        var casePreAcceptType4 = new CasePreAcceptType();
        casePreAcceptType4.setDateAccepted("2020-08-07");
        caseData4.setPreAcceptCase(casePreAcceptType4);
        caseData4.setCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem4 = new BFActionTypeItem();
        var bfActionType4 = new BFActionType();
        bfActionType4.setBfDate("2020-08-10");
        bfActionType4.setNotes("Test Notes Four");
        bfActionTypeItem4.setId("0014");
        bfActionTypeItem4.setValue(bfActionType4);
        caseData4.setBfActions(new ArrayList<>(Arrays.asList(bfActionTypeItem4)));
        caseData4.setClaimServedDate("2020-08-15");
        submitEvent4.setCaseData(caseData4);

        // Case without BF Action & Claim Served Date
        var submitEvent5 = new SubmitEvent();
        submitEvent5.setCaseId(5);
        submitEvent5.setState(ACCEPTED_STATE);

        var caseData5 = new CaseData();
        caseData5.setEthosCaseReference("1800528/2020");
        caseData5.setReceiptDate("2020-08-19");
        var casePreAcceptType5 = new CasePreAcceptType();
        casePreAcceptType5.setDateAccepted("2020-08-07");
        caseData5.setPreAcceptCase(casePreAcceptType5);
        caseData5.setCaseType(SINGLE_CASE_TYPE);

        var bfActionTypeItem5 = new BFActionTypeItem();
        var bfActionType5 = new BFActionType();
        bfActionType5.setBfDate("2020-08-19");
        bfActionType5.setNotes("Test Notes Five");
        bfActionTypeItem5.setId("0014");
        bfActionTypeItem5.setValue(bfActionType5);
        caseData5.setBfActions(new ArrayList<>(Arrays.asList(bfActionTypeItem5)));
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
        var resultListingData = servingClaimsReport.generateReportData(listingDetailsRange, submitEvents);
        var caseTotalCount =  resultListingData.getLocalReportsDetail().get(0)
                .getValue().getClaimServedTotal();
        var actualCount = Strings.isNullOrEmpty(caseTotalCount)  ? 0 : Integer.parseInt(caseTotalCount);
        assertEquals(5, actualCount);
    }

    @Test
    public void shouldReturnCorrectCasesCountByServingDay() {
        var servingClaimsReport = new ServingClaimsReport();
        var resultListingData = servingClaimsReport.generateReportData(listingDetailsRange, submitEvents);
        var actualCaseCount =  resultListingData.getLocalReportsDetail().get(0)
                .getValue().getClaimServedTotal();
        var adhocReportType =  resultListingData.getLocalReportsDetail().get(0)
                .getValue();
        var claimServedItems = adhocReportType.getClaimServedItems();
        var expectedDay1Count = claimServedItems.stream()
                .filter(x->Integer.parseInt(x.getValue().getReportedNumberOfDays()) == 0).count();
        var expectedDay2Count = claimServedItems.stream()
                .filter(x->Integer.parseInt(x.getValue().getReportedNumberOfDays()) == 1).count();
        var expectedDay3Count = claimServedItems.stream()
                .filter(x->Integer.parseInt(x.getValue().getReportedNumberOfDays()) == 2).count();
        var expectedDay4Count = claimServedItems.stream()
                .filter(x->Integer.parseInt(x.getValue().getReportedNumberOfDays()) == 3).count();
        var expectedDay5Count = claimServedItems.stream()
                .filter(x->Integer.parseInt(x.getValue().getReportedNumberOfDays()) == 4).count();
        var expectedDay6PlusCount = claimServedItems.stream()
                .filter(x->Integer.parseInt(x.getValue().getReportedNumberOfDays()) >= 5).count();

        assertEquals(2, expectedDay1Count);
        assertEquals(0, expectedDay2Count);
        assertEquals(1, expectedDay3Count);
        assertEquals(1, expectedDay4Count);
        assertEquals(0, expectedDay5Count);
        assertEquals(1, expectedDay6PlusCount);
    }
}
