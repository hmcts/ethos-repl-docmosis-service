package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

public class BfActionReportTest {
    private List<SubmitEvent> submitEvents;
    private ListingDetails listingDetails;
    private ListingData listingData;
    private BfActionReport bfActionReport;
    private SubmitEvent submitEvent;
    private CaseData caseData;

    @Before
    public void setUp() {
        listingDetails = new ListingDetails();
        listingData = new ListingData();
        listingData.setReportType(BROUGHT_FORWARD_REPORT);
        listingDetails.setJurisdiction("EMPLOYMENT");
        listingData.setListingVenue("Leeds");
        listingDetails.setCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID);
        submitEvents = new ArrayList<>();
        listingData = new ListingData();
        listingData.setListingDateFrom("2019-12-08");
        listingData.setListingDateTo("2019-12-20");
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);
        bfActionReport = new BfActionReport();
        submitEvent = new SubmitEvent();
        submitEvent.setCaseId(2);
        submitEvent.setState(ACCEPTED_STATE);
        caseData = new CaseData();
        caseData.setEthosCaseReference("1800522/2020");
        caseData.setReceiptDate("2018-08-10");
        var casePreAcceptType2 = new CasePreAcceptType();
        casePreAcceptType2.setDateAccepted("2018-08-10");
        caseData.setPreAcceptCase(casePreAcceptType2);
        caseData.setEcmCaseType(SINGLE_CASE_TYPE);
    }

    @Test
    public void shouldReturnOnlyOpenBfActionsWithInDateRange() {

        var bfActionTypeItem = new BFActionTypeItem();
        bfActionTypeItem.setId("123");
        var bFActionType = new BFActionType();
        bFActionType.setCwActions("Case papers prepared");
        bFActionType.setBfDate("2019-12-18");
        bFActionType.setDateEntered("2019-11-20");
        bFActionType.setNotes("test comment one");
        bfActionTypeItem.setValue(bFActionType);

        var bfActionTypeItem2 = new BFActionTypeItem();
        bfActionTypeItem2.setId("456");
        var bFActionType2 = new BFActionType();
        bFActionType2.setCwActions("Interlocutory order requested");
        bFActionType2.setBfDate("2019-12-05");
        bFActionType2.setDateEntered("2019-11-20");
        bFActionType2.setNotes("test cleared bf");
        bfActionTypeItem2.setValue(bFActionType2);

        var bfActionTypeItem3 = new BFActionTypeItem();
        bfActionTypeItem3.setId("456");
        var bFActionType3 = new BFActionType();
        bFActionType3.setCwActions("Interlocutory new order requested");
        bFActionType3.setBfDate("2019-12-08");
        bFActionType3.setDateEntered("2019-11-20");
        bFActionType3.setNotes("test non-cleared bf two");
        bfActionTypeItem3.setValue(bFActionType3);

        var bfActionTypeItem4 = new BFActionTypeItem();
        bfActionTypeItem4.setId("789");
        var bFActionType4 = new BFActionType();
        bFActionType4.setCwActions("Application of letter to ACAS/RPO");
        bFActionType4.setBfDate("2019-12-14");
        bFActionType4.setDateEntered("2019-11-20");
        bFActionType4.setNotes("test non-cleared bf three");
        bfActionTypeItem4.setValue(bFActionType4);

        List<BFActionTypeItem> items = new ArrayList<>();
        items.add(bfActionTypeItem);
        items.add(bfActionTypeItem2);
        items.add(bfActionTypeItem3);
        items.add(bfActionTypeItem4);

        caseData.setBfActions(items);
        submitEvent.setCaseData(caseData);
        submitEvents.add(submitEvent);

        var resultListingData = bfActionReport.runReport(listingDetails, submitEvents, "userName");
        var actualBfDateCount  = resultListingData.getBfDateCollection().size();
        var expectedBfDateCount = 3;
        assertEquals(expectedBfDateCount, actualBfDateCount);

        var firstBFDateTypeItem = resultListingData.getBfDateCollection().get(0).getValue();
        assertEquals("2019-12-08", firstBFDateTypeItem.getBroughtForwardDate());
        assertEquals(bFActionType3.getCwActions(), firstBFDateTypeItem.getBroughtForwardAction());
        assertEquals("2019-11-20", firstBFDateTypeItem.getBroughtForwardEnteredDate());
        assertEquals(bFActionType3.getNotes(), firstBFDateTypeItem.getBroughtForwardDateReason());
        assertEquals(bFActionType3.getCleared(), firstBFDateTypeItem.getBroughtForwardDateCleared());

        var clearedBfDates = resultListingData.getBfDateCollection().stream()
            .filter(item -> !StringUtils.isBlank(item.getValue().getBroughtForwardDateCleared()));
        assertEquals(0, clearedBfDates.count());
    }

    @Test
    public void shouldReturnBfActionsWithOnlyDateAndTimeInBfDate() {
        listingData.setListingDateFrom("2019-12-08");
        listingData.setListingDateTo("2019-12-20");
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        // Two bf action entries added and the second BFActionTypeItem has
        // the "YYYY-MM-DD HH:MM:SS" date and time pattern without milliseconds for the BfDate
        List<BFActionTypeItem> items = getBFActionTypeItems();
        caseData.setBfActions(items);
        submitEvent.setCaseData(caseData);
        submitEvents.add(submitEvent);

        var resultListingData = bfActionReport.runReport(listingDetails, submitEvents, "userName");
        var expectedBfDateCount = 2;
        assertEquals(expectedBfDateCount, resultListingData.getBfDateCollection().size());
    }

    @Test
    public void shouldNotReturnClearedBfActionsWithInDateRange() {
        // Given three BFActionTypeItems, where the first two are still open and the third
        // one is with cleared BF status, only the two open (i.e. not cleared) BFs should be returned.
        List<BFActionTypeItem> items = getBFActionTypeItems();

        var bfActionTypeItem4 = new BFActionTypeItem();
        bfActionTypeItem4.setId("116");
        var bFActionType4 = new BFActionType();
        bFActionType4.setCwActions("Interlocutory order requested");
        bFActionType4.setBfDate("2019-12-13");
        bFActionType4.setDateEntered("2019-11-20");
        bFActionType4.setCleared("2019-12-24");
        bFActionType4.setNotes("test case with cleared bfs");
        bfActionTypeItem4.setValue(bFActionType4);
        items.add(bfActionTypeItem4);
        caseData.setBfActions(items);
        submitEvent.setCaseData(caseData);
        submitEvents.add(submitEvent);

        var resultListingData = bfActionReport.runReport(listingDetails, submitEvents, "userName");
        var expectedBfDateCount = 2;
        assertEquals(expectedBfDateCount, resultListingData.getBfDateCollection().size());
    }

    @Test
    public void shouldReturnBfActionsSortedByBfDate() {
        listingData.setListingDateFrom("2019-12-08");
        listingData.setListingDateTo("2019-12-25");
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);
        List<BFActionTypeItem> items = getBFActionTypeItems();

        var bfActionTypeItem6 = new BFActionTypeItem();
        bfActionTypeItem6.setId("456");
        var bFActionType6 = new BFActionType();
        bFActionType6.setCwActions("Interlocutory new order requested");
        bFActionType6.setBfDate("2019-12-08");
        bFActionType6.setDateEntered("2019-11-20");
        bFActionType6.setNotes("test non-cleared bf sixth");
        bfActionTypeItem6.setValue(bFActionType6);
        items.add(bfActionTypeItem6);
        caseData.setBfActions(items);
        submitEvent.setCaseData(caseData);
        submitEvents.add(submitEvent);

        var bfActionReport = new BfActionReport();
        var resultListingData = bfActionReport.runReport(listingDetails, submitEvents, "userName");
        // bFActionType3 is added last. But it has the earliest bfDate. As the returned listingData from
        // bfActionReport.runReport method call should be ordered by bfDate, bFActionType3
        // should be the first element
        var bFActionType3 = items.get(2).getValue();
        var firstBFDateTypeItem = resultListingData.getBfDateCollection().get(0).getValue();
        assertEquals("2019-12-08", firstBFDateTypeItem.getBroughtForwardDate());
        assertEquals(bFActionType3.getCwActions(), firstBFDateTypeItem.getBroughtForwardAction());
        assertEquals(bFActionType3.getNotes(), firstBFDateTypeItem.getBroughtForwardDateReason());
        assertEquals("2019-11-20", firstBFDateTypeItem.getBroughtForwardEnteredDate());
        assertEquals(bFActionType3.getCleared(), firstBFDateTypeItem.getBroughtForwardDateCleared());
    }

    @Test
    public void shouldReturnBfActionsWithFormattedComment() {
        listingData.setListingDateFrom("2019-12-03");
        listingData.setListingDateTo("2019-12-28");
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<BFActionTypeItem> items = getBFActionTypeItems();

        var bfActionTypeItemFour = new BFActionTypeItem();
        bfActionTypeItemFour.setId("1488");
        var bFActionTypeFour = new BFActionType();
        bFActionTypeFour.setCwActions("Another order requested");
        bFActionTypeFour.setBfDate("2019-12-07");
        bFActionTypeFour.setDateEntered("2019-11-25");
        bFActionTypeFour.setNotes("test non-cleared bf two\n Second line comment");
        bfActionTypeItemFour.setValue(bFActionTypeFour);

        items.add(bfActionTypeItemFour);
        caseData.setBfActions(items);
        submitEvent.setCaseData(caseData);
        submitEvents.add(submitEvent);

        var bfActionReport = new BfActionReport();
        var resultListingData = bfActionReport.runReport(listingDetails, submitEvents, "userName");
        //Because the Bf entries are sorted by bf date, bfActionTypeItemFour is the first entry in the
        //result listing data
        var firstBFDateTypeItem = resultListingData.getBfDateCollection().get(0).getValue();
        var correctedComment = bfActionTypeItemFour.getValue().getNotes()
            .replace("\n", ". ");
        assertEquals(correctedComment, firstBFDateTypeItem.getBroughtForwardDateReason());
    }

    private List<BFActionTypeItem> getBFActionTypeItems() {

        var bfActionTypeItem3 = new BFActionTypeItem();
        bfActionTypeItem3.setId("116");
        var bFActionType3 = new BFActionType();
        bFActionType3.setCwActions("Interlocutory order requested");
        bFActionType3.setBfDate("2019-12-13");
        bFActionType3.setDateEntered("2019-11-20");
        bFActionType3.setNotes("test non-cleared bf two");
        bfActionTypeItem3.setValue(bFActionType3);

        var bfActionTypeItem4 = new BFActionTypeItem();
        bfActionTypeItem4.setId("99456");
        var bFActionType4 = new BFActionType();
        bFActionType4.setCwActions("Interlocutory new order requested");
        bFActionType4.setBfDate("2019-12-16");
        bFActionType4.setDateEntered("2019-11-23");
        bFActionType4.setNotes("test another non-cleared bf three");
        bfActionTypeItem4.setValue(bFActionType4);

        List<BFActionTypeItem> items = new ArrayList<>();
        items.add(bfActionTypeItem3);
        items.add(bfActionTypeItem4);

        return items;
    }
}
