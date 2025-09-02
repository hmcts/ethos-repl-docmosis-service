package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;

class ClaimsByHearingVenueReportTest {
    ClaimsByHearingVenueReportDataSource claimsByHearingVenueReportDataSource;
    private ClaimsByHearingVenueReport claimsByHearingVenueReport;
    ClaimsByHearingVenueCaseDataBuilder caseDataBuilder = new ClaimsByHearingVenueCaseDataBuilder();
    List<ClaimsByHearingVenueSubmitEvent> submitEvents = new ArrayList<>();
    ClaimsByHearingVenueReportParams reportParams;
    static final String RANGE_START_DATE = "2021-12-02T01:00:00.000";
    static final String RANGE_END_DATE = "2021-12-28T23:59:59.000";
    static final String SINGLE_START_DATE = "2021-12-08T01:00:00.000";
    static final String SINGLE_END_DATE = "2021-12-08T23:59:59.000";
    static final String TEST_USERNAME = "ECM Tester";
    static final String OFFICE_NAME = "Leeds";

    @BeforeEach
    public void setUp() {
        submitEvents.clear();
        caseDataBuilder = new ClaimsByHearingVenueCaseDataBuilder();
        claimsByHearingVenueReportDataSource = mock(ClaimsByHearingVenueReportDataSource.class);
        claimsByHearingVenueReport = new ClaimsByHearingVenueReport(claimsByHearingVenueReportDataSource);
        reportParams = new ClaimsByHearingVenueReportParams(LEEDS_LISTING_CASE_TYPE_ID, RANGE_START_DATE,
                RANGE_END_DATE, RANGE_HEARING_DATE_TYPE, TEST_USERNAME);
    }

    @Test
    void shouldShowCorrectNumberOfReportDetailEntriesForDateRangeSearch() {
        // Given cases have date of Receipt value within the inclusive date range searched for
        // When report data is requested
        // Then only all cases with valid date should be in the report data detail entries

        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var submitEventOne = caseDataBuilder
                .withEthosCaseReference("18000012/2022")
                .withReceiptDate("2021-12-14")
                .withClaimantType(claimant)
                .withClaimantWorkAddressType(null)
                .withRespondentCollection(null)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventOne);

        var submitEventTwo = caseDataBuilder
                .withEthosCaseReference("18000013/2022")
                .withReceiptDate("2021-12-08")
                .withClaimantType(claimant)
                .withClaimantWorkAddressType(null)
                .withRespondentCollection(null)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventTwo);

        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), RANGE_START_DATE, RANGE_END_DATE))
                .thenReturn(submitEvents);

        var expectedReportTitle = getReportTitle(RANGE_HEARING_DATE_TYPE);
        var expectedNumberOfSubmitEventEntries = submitEvents.size();
        var reportData = claimsByHearingVenueReport
                .generateReport(reportParams,"Leeds");
        var actualReportTitle = reportData.getReportPeriodDescription();

        assertEquals(expectedReportTitle, actualReportTitle);
        assertEquals(expectedNumberOfSubmitEventEntries, reportData.getReportDetails().size());
    }

    @Test
    void shouldShowCorrectNumberOfReportDetailEntriesForSingleDateSearch() {
        // Given cases have date of Receipt value on the single date searched for
        // When report data is requested
        // Then only all cases with matching Receipt date should be in the report data detail entries

        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var submitEventOne = caseDataBuilder
                .withEthosCaseReference("18000012/2022")
                .withReceiptDate("2021-12-08")
                .withClaimantType(claimant)
                .withClaimantWorkAddressType(null)
                .withRespondentCollection(null)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventOne);

        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), SINGLE_START_DATE, SINGLE_END_DATE))
                .thenReturn(submitEvents);

        var expectedReportTitle = getReportTitle(SINGLE_HEARING_DATE_TYPE);
        var expectedNumberOfSubmitEventEntries = 1;
        var singleHearingDateTypeReportParams = new ClaimsByHearingVenueReportParams(LEEDS_LISTING_CASE_TYPE_ID,
                SINGLE_START_DATE, SINGLE_END_DATE, SINGLE_HEARING_DATE_TYPE, TEST_USERNAME);
        var reportData = claimsByHearingVenueReport
                .generateReport(singleHearingDateTypeReportParams, "Leeds");
        var actualReportTitle = reportData.getReportPeriodDescription();

        assertEquals(expectedReportTitle, actualReportTitle);
        assertEquals(expectedNumberOfSubmitEventEntries, reportData.getReportDetails().size());
    }

    @Test
    void shouldShowNullStringValueForPostcodeWhenClaimantWorkAddressNotSet() {
        // Given a case has Claimant Work Address not set or is null
        // When report data is requested
        // Then on all cases with valid date, "Null" should be used for postcode in the report data detail entries
        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var submitEventOne = caseDataBuilder
            .withEthosCaseReference("18000012/2022")
            .withReceiptDate("2021-12-14")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(null)
            .withRespondentCollection(null)
            .buildAsSubmitEvent(ACCEPTED_STATE);
    submitEvents.add(submitEventOne);
        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), RANGE_START_DATE, RANGE_END_DATE))
                .thenReturn(submitEvents);

        var expectedClaimantAddressUKPostcode = "DH3 8HL";
        var expectedClaimantWorkPostcode = "Null";
        var expectedRespondentPostcode = "Null";
        var expectedRespondentET3Postcode = "Null";
        var reportData = claimsByHearingVenueReport
                .generateReport(reportParams, "Manchester");
        var actualClaimantAddressUKPostcode = reportData.getReportDetails().get(0).getClaimantPostcode();
        var actualClaimantWorkPostcode = reportData.getReportDetails().get(0).getClaimantWorkPostcode();
        var actualRespondentPostcode = reportData.getReportDetails().get(0).getRespondentPostcode();
        var actualRespondentET3Postcode = reportData.getReportDetails().get(0).getRespondentET3Postcode();

        assertEquals(expectedClaimantAddressUKPostcode, actualClaimantAddressUKPostcode);
        assertEquals(expectedClaimantWorkPostcode, actualClaimantWorkPostcode);
        assertEquals(expectedRespondentPostcode, actualRespondentPostcode);
        assertEquals(expectedRespondentET3Postcode, actualRespondentET3Postcode);
    }

    @Test
    void shouldShowNullStringValueForMissingPostCodeInClaimantWorkAddressProvided() {
        // Given a case has a Claimant Work Address provided and postcode in it is not set or is null
        // When report data is requested
        // Then on all cases with valid date, "Null" should be used for postcode in the report data detail entries
        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var claimantWorkAddress = new Address();
        claimantAddressUK.setPostCode(null);
        var claimantWorkAddressType = new ClaimantWorkAddressType();
        claimantWorkAddressType.setClaimantWorkAddress(claimantWorkAddress);

        var submitEventOne = caseDataBuilder
            .withEthosCaseReference("18000012/2022")
            .withReceiptDate("2021-12-14")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(claimantWorkAddressType)
            .withRespondentCollection(null)
            .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventOne);

        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), RANGE_START_DATE, RANGE_END_DATE))
                .thenReturn(submitEvents);

        var expectedClaimantAddressUKPostcode = "Null";
        var expectedClaimantWorkPostcode = "Null";
        var expectedRespondentPostcode = "Null";
        var expectedRespondentET3Postcode = "Null";
        var reportData = claimsByHearingVenueReport
                .generateReport(reportParams, "Manchester");

        var actualClaimantAddressUKPostcode = reportData.getReportDetails().get(0).getClaimantPostcode();
        var actualClaimantWorkPostcode = reportData.getReportDetails().get(0).getClaimantWorkPostcode();
        var actualRespondentPostcode = reportData.getReportDetails().get(0).getRespondentPostcode();
        var actualRespondentET3Postcode = reportData.getReportDetails().get(0).getRespondentET3Postcode();

        assertEquals(expectedClaimantAddressUKPostcode, actualClaimantAddressUKPostcode);
        assertEquals(expectedClaimantWorkPostcode, actualClaimantWorkPostcode);
        assertEquals(expectedRespondentPostcode, actualRespondentPostcode);
        assertEquals(expectedRespondentET3Postcode, actualRespondentET3Postcode);
    }

    @Test
    void shouldShowFirstRespondentPostCodeOrNullString() {
        // Given a case has a number of respondents
        // When report data is requested
        // Then only the postcode of the first respondent detail should be used to set "Respondent Postcode"
        // and "Respondent ET3 Postcode" values in the report data detail entry. "Null" should be used if
        // no postcodes found
        List<RespondentSumTypeItem> respondentCollection = new ArrayList<>();
        var respondentSumTypeItem = new RespondentSumTypeItem();
        var respondentSumType = new RespondentSumType();
        var firstRespondentAddress = new Address();
        firstRespondentAddress.setPostCode("DH1 1AE");
        respondentSumType.setRespondentAddress(firstRespondentAddress);

        var firstRespondentET3Address = new Address();
        firstRespondentET3Address.setPostCode("DH1 1AJ");
        respondentSumType.setResponseRespondentAddress(firstRespondentET3Address);
        respondentSumTypeItem.setValue(respondentSumType);
        respondentCollection.add(respondentSumTypeItem);

        var respondentSumTypeItemTwo = new RespondentSumTypeItem();
        var respondentSumTypeTwo = new RespondentSumType();
        var secondRespondentAddress = new Address();
        secondRespondentAddress.setPostCode("DH5 9AJ");
        respondentSumTypeTwo.setRespondentAddress(secondRespondentAddress);
        respondentSumTypeItemTwo.setValue(respondentSumTypeTwo);
        respondentCollection.add(respondentSumTypeItemTwo);

        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var submitEventOne = caseDataBuilder
            .withEthosCaseReference("18000012/2022")
            .withReceiptDate("2021-12-14")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(null)
            .withRespondentCollection(respondentCollection)
            .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventOne);
        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), RANGE_START_DATE, RANGE_END_DATE))
                .thenReturn(submitEvents);

        var expectedReportDetailEntriesCount = submitEvents.size();
        var expectedRespondentPostCode = firstRespondentAddress.getPostCode();
        var expectedFirstRespondentET3AddressPostCode = firstRespondentET3Address.getPostCode();
        var reportData = claimsByHearingVenueReport
                .generateReport(reportParams, "Manchester");
        var actualRespondentPostCode = reportData.getReportDetails().get(0).getRespondentPostcode();
        var actualRespondentET3PostCode = reportData.getReportDetails().get(0).getRespondentET3Postcode();

        assertEquals(expectedReportDetailEntriesCount, reportData.getReportDetails().size());
        assertEquals(expectedRespondentPostCode, actualRespondentPostCode);
        assertEquals(expectedFirstRespondentET3AddressPostCode, actualRespondentET3PostCode);
    }

    @Test
    void shouldShowReportDetailEntriesSortedByEthosCaseReferenceAscending() {
        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var submitEventOne = caseDataBuilder
            .withEthosCaseReference("18000012/2022")
            .withReceiptDate("2021-12-14")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(null)
            .withRespondentCollection(null)
            .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventOne);

        var submitEventTwo = caseDataBuilder
            .withEthosCaseReference("1800154/2021")
            .withReceiptDate("2021-12-08")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(null)
            .withRespondentCollection(null)
            .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventTwo);

        var submitEventThree = caseDataBuilder
            .withEthosCaseReference("18000003/2022")
            .withReceiptDate("2021-12-08")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(null)
            .withRespondentCollection(null)
            .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventThree);

        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), RANGE_START_DATE, RANGE_END_DATE))
                .thenReturn(submitEvents);

        var expectedNumberOfSubmitEventEntries = submitEvents.size();
        var expectedFirstCaseReference = submitEvents.get(1).getCaseData().getEthosCaseReference();
        var expectedSecondCaseReference = submitEvents.get(2).getCaseData().getEthosCaseReference();
        var expectedThirdFirstCaseReference = submitEvents.get(0).getCaseData().getEthosCaseReference();
        var reportData = claimsByHearingVenueReport
                .generateReport(reportParams, "Manchester");

        assertEquals(expectedNumberOfSubmitEventEntries, reportData.getReportDetails().size());
        assertEquals(expectedFirstCaseReference, reportData.getReportDetails().get(0).getCaseReference());
        assertEquals(expectedSecondCaseReference, reportData.getReportDetails().get(1).getCaseReference());
        assertEquals(expectedThirdFirstCaseReference, reportData.getReportDetails().get(2).getCaseReference());
    }

    @Test
    void shouldShowCorrectReportPrintedOnDescription() {
        // Given cases have date of Receipt value within the inclusive date range searched for
        // When report data is requested
        // Then excel report should print correct value for "ReportPrintedOnDescription" field

        var claimantAddressUK = new Address();
        claimantAddressUK.setPostCode("DH3 8HL");
        var claimant = new ClaimantType();
        claimant.setClaimantAddressUK(claimantAddressUK);

        var submitEventOne = caseDataBuilder
            .withEthosCaseReference("18000012/2022")
            .withReceiptDate("2021-12-08")
            .withClaimantType(claimant)
            .withClaimantWorkAddressType(null)
            .withRespondentCollection(null)
            .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEventOne);
        var singleHearingDateTypeReportParams = new ClaimsByHearingVenueReportParams(LEEDS_LISTING_CASE_TYPE_ID,
                SINGLE_START_DATE, SINGLE_END_DATE, SINGLE_HEARING_DATE_TYPE, TEST_USERNAME);
        when(claimsByHearingVenueReportDataSource.getData(
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), SINGLE_START_DATE, SINGLE_END_DATE))
                .thenReturn(submitEvents);

        var expectedReportPrintedOnDescription = getTestReportPrintedDescription();
        var reportData = claimsByHearingVenueReport
                .generateReport(singleHearingDateTypeReportParams, "Manchester");
        var actualReportTitle = reportData.getReportPrintedOnDescription();

        assertEquals(expectedReportPrintedOnDescription, actualReportTitle);
    }

    private String getReportTitle(String reportDateType) {
        if (SINGLE_HEARING_DATE_TYPE.equals(reportDateType)) {
            return "   Period: " + getExpectedSingleDateReportTitle() + "       Office: " + OFFICE_NAME;
        } else {
            return "   Period: " + getExpectedDateRangeReportTitle() + "       Office: " + OFFICE_NAME;
        }
    }

    private String getExpectedDateRangeReportTitle() {
        return "Between " + UtilHelper.listingFormatLocalDate(ReportHelper.getFormattedLocalDate(RANGE_START_DATE))
                + " and " + UtilHelper.listingFormatLocalDate(ReportHelper.getFormattedLocalDate(RANGE_END_DATE));
    }

    private String getExpectedSingleDateReportTitle() {
        return "On " + UtilHelper.listingFormatLocalDate(ReportHelper.getFormattedLocalDate(SINGLE_START_DATE));
    }

    private String getTestReportPrintedDescription() {
        return "Reported on: " + UtilHelper.formatCurrentDate(LocalDate.now()) + "   By: " + TEST_USERNAME;
    }
}
