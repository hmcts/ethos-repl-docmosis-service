package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import org.assertj.core.util.Strings;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesourcelocalreport.CaseSourceLocalReport;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_ECC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIGRATION_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;

public class CaseSourceLocalReportTest {
    @Test
    public void testReportHeaderTotalsAreZeroIfNoCasesExist() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();
        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        CaseSourceLocalReport caseSourceLocalReport = new CaseSourceLocalReport();
        ListingData listingData = caseSourceLocalReport.generateReportData(listingDetails, submitEvents);
        verifyReportHeaderIsZero(listingData);
    }

    private void verifyReportHeaderIsZero(ListingData listingData) {
        AdhocReportType adhocReportType = listingData.getLocalReportsSummary().getFirst().getValue();
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getEt1OnlineTotalCases()) ? 0 :
            Integer.parseInt(adhocReportType.getEt1OnlineTotalCases()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getMigratedTotalCases()) ? 0 :
            Integer.parseInt(adhocReportType.getMigratedTotalCases()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getEccTotalCases()) ? 0 :
            Integer.parseInt(adhocReportType.getEccTotalCases()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getManuallyCreatedTotalCases()) ? 0 :
            Integer.parseInt(adhocReportType.getManuallyCreatedTotalCases()));
        assertEquals(0.00, Strings.isNullOrEmpty(adhocReportType.getManuallyCreatedTotalCasesPercent()) ? 0.00 :
            Float.parseFloat(adhocReportType.getManuallyCreatedTotalCasesPercent()), .00);
        assertEquals(0.00, Strings.isNullOrEmpty(adhocReportType.getEt1OnlineTotalCasesPercent()) ? 0.00 :
            Float.parseFloat(adhocReportType.getEt1OnlineTotalCasesPercent()), .00);
        assertEquals(0.00, Strings.isNullOrEmpty(adhocReportType.getMigratedTotalCasesPercent()) ? 0.00 :
            Float.parseFloat(adhocReportType.getMigratedTotalCasesPercent()), .00);
        assertEquals(0.00, Strings.isNullOrEmpty(adhocReportType.getEccTotalCasesPercent()) ? 0.00 :
            Float.parseFloat(adhocReportType.getEccTotalCasesPercent()), .00);

    }

    @Test
    public void mainTest() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent("1970-04-01", MANUALLY_CREATED_POSITION));
        submitEvents.add(createSubmitEvent("1970-04-02", MIGRATION_CASE_SOURCE));
        submitEvents.add(createSubmitEvent("1970-04-03", ET1_ONLINE_CASE_SOURCE));
        submitEvents.add(createSubmitEvent("1970-04-04", FLAG_ECC));

        CaseSourceLocalReport caseSourceLocalReport = new CaseSourceLocalReport();
        ListingData reportListingData = caseSourceLocalReport.generateReportData(listingDetails, submitEvents);

        AdhocReportType adhocReportType = reportListingData.getLocalReportsSummary().getFirst().getValue();
        assertEquals(4, Strings.isNullOrEmpty(adhocReportType.getTotalCases()) ? 0 : Integer.parseInt(
            adhocReportType.getTotalCases()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getMigratedTotalCases()) ? 0 : Integer.parseInt(
            adhocReportType.getMigratedTotalCases()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getEccTotalCases()) ? 0 : Integer.parseInt(
            adhocReportType.getEccTotalCases()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getManuallyCreatedTotalCases()) ? 0 : Integer.parseInt(
            adhocReportType.getManuallyCreatedTotalCases()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getEt1OnlineTotalCases()) ? 0 : Integer.parseInt(
            adhocReportType.getEt1OnlineTotalCases()));
        assertEquals(25, Strings.isNullOrEmpty(adhocReportType.getManuallyCreatedTotalCasesPercent()) ? 0 :
            Float.parseFloat(
            adhocReportType.getManuallyCreatedTotalCasesPercent()), .00);
        assertEquals(25, Strings.isNullOrEmpty(adhocReportType.getEt1OnlineTotalCasesPercent()) ? 0 : Float.parseFloat(
            adhocReportType.getEt1OnlineTotalCasesPercent()), .00);
        assertEquals(25, Strings.isNullOrEmpty(adhocReportType.getEccTotalCasesPercent()) ? 0 : Float.parseFloat(
            adhocReportType.getEccTotalCasesPercent()), .00);
        assertEquals(25, Strings.isNullOrEmpty(adhocReportType.getMigratedTotalCasesPercent()) ? 0 : Float.parseFloat(
            adhocReportType.getMigratedTotalCasesPercent()), .00);
    }

    private SubmitEvent createSubmitEvent(String receiptDate, String caseSource) {
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setReceiptDate(receiptDate);
        caseData.setCaseSource(caseSource);
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }
}
