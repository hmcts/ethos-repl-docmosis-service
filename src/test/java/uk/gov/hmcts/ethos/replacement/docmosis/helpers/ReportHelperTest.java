package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;

public class ReportHelperTest {

    @Test
    public void shouldReturnNullForInputWithLengthLessThanTenChars() {
        assertNull(ReportHelper.getFormattedLocalDate("2021-3-2"));
    }

    @Test
    public void shouldReturnNullForUnParseableInput() {
        // When un-parseable input string is supplied, an exception is thrown and handled.
        // After logging an error message, null is returned.
        assertNull(ReportHelper.getFormattedLocalDate("2021-06-25BAD 21:12:12"));
    }

    @Test
    public void shouldReturnLocalDateForValidInput() {
        var inputDateTimeWithMillisecondsAndDateTimeSeparator = "2021-12-12T21:12:12.000";
        var expectedLocalDate = "2021-12-12";
        assertEquals(expectedLocalDate, getActual(inputDateTimeWithMillisecondsAndDateTimeSeparator));

        var inputDateTimeWithOnlyDateTimeSeparator = "2021-10-18T21:12:12";
        var expectedLocalDateTwo = "2021-10-18";
        assertEquals(expectedLocalDateTwo, getActual(inputDateTimeWithOnlyDateTimeSeparator));

        var inputDateTimeWithBlankSpace = "2021-06-25 21:12:12";
        var expectedLocalDateThree = "2021-06-25";
        assertEquals(expectedLocalDateThree, getActual(inputDateTimeWithBlankSpace));
    }

    private String getActual(String inputDateTime) {
        return ReportHelper.getFormattedLocalDate(inputDateTime);
    }

    @Test
    public void testProcessClaimsAcceptedRequest_WithMatchingPreAcceptDate() {
        ListingDetails listingDetails = generateListingDetails();
        List<SubmitEvent> submitEvents = List.of(
            generateSubmitEvent(ACCEPTED_STATE),
            generateSubmitEvent(ACCEPTED_STATE)
        );
        ListingData result = ReportHelper.processClaimsAcceptedRequest(listingDetails, submitEvents);
        assertEquals(2, result.getLocalReportsDetail().size());
        assertEquals("2", result.getLocalReportsDetailHdr().getTotal());
        assertEquals("2", result.getLocalReportsDetailHdr().getSinglesTotal());
        assertEquals("0", result.getLocalReportsDetailHdr().getMultiplesTotal());
    }

    @Test
    public void testProcessClaimsAcceptedRequest_WithTransferredState() {
        ListingDetails listingDetails = generateListingDetails();
        List<SubmitEvent> submitEvents = List.of(
            generateSubmitEvent(ACCEPTED_STATE),
            generateSubmitEvent(TRANSFERRED_STATE)
        );
        ListingData result = ReportHelper.processClaimsAcceptedRequest(listingDetails, submitEvents);
        assertEquals(1, result.getLocalReportsDetail().size());
        assertEquals("1", result.getLocalReportsDetailHdr().getTotal());
        assertEquals("1", result.getLocalReportsDetailHdr().getSinglesTotal());
        assertEquals("0", result.getLocalReportsDetailHdr().getMultiplesTotal());
    }

    private ListingDetails generateListingDetails() {
        ListingData listingData = new ListingData();
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingData.setListingDateFrom("2025-05-01");
        listingData.setListingDateTo("2025-05-31");

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.setCaseData(listingData);

        return listingDetails;
    }

    private SubmitEvent generateSubmitEvent(String state) {
        CasePreAcceptType preAccept = new CasePreAcceptType();
        preAccept.setDateAccepted("2025-05-11");

        CaseData caseData = new CaseData();
        caseData.setPreAcceptCase(preAccept);
        caseData.setEcmCaseType(SINGLE_CASE_TYPE);

        SubmitEvent event = new SubmitEvent();
        event.setState(state);
        event.setCaseData(caseData);

        return event;
    }
}
