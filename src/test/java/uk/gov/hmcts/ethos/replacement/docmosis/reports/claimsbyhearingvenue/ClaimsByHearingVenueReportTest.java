package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

class ClaimsByHearingVenueReportTest {
    ClaimsByHearingVenueReportDataSource claimsByHearingVenueReportDataSource;
    ClaimsByHearingVenueReport claimsByHearingVenueReport;
    ClaimsByHearingVenueCaseDataBuilder caseDataBuilder = new ClaimsByHearingVenueCaseDataBuilder();
    List<ClaimsByHearingVenueSubmitEvent> submitEvents = new ArrayList<>();
    List<RespondentSumTypeItem> respondentCollection = new ArrayList<>();
    ReportParams params;
    static final String START_DATE = "2021-12-02";
    static final String END_DATE = "2021-12-28";

    @BeforeEach
    public void setUp() {
        submitEvents.clear();
        caseDataBuilder = new ClaimsByHearingVenueCaseDataBuilder();
        claimsByHearingVenueReportDataSource = mock(ClaimsByHearingVenueReportDataSource.class);
        when(claimsByHearingVenueReportDataSource.getData(LEEDS_LISTING_CASE_TYPE_ID,
            START_DATE, END_DATE)).thenReturn(submitEvents);
        params = new ReportParams(LEEDS_LISTING_CASE_TYPE_ID, START_DATE, END_DATE);
        claimsByHearingVenueReport = new ClaimsByHearingVenueReport(claimsByHearingVenueReportDataSource, params);
    }

    @Test
    void shouldShowCorrectNumberOfReportDetailEntries() {
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
            UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), START_DATE, END_DATE))
            .thenReturn(submitEvents);
        var expectedNumberOfSubmitEventEntries = submitEvents.size();
        var reportData = claimsByHearingVenueReport
            .generateReport(LEEDS_LISTING_CASE_TYPE_ID, null);
        assertEquals(expectedNumberOfSubmitEventEntries, reportData.getReportDetails().size());
    }

    @Test
    void shouldShowNullStringValueForPostcodeWhenClaimantWorkAddressNotSet() {
        // Given a case has Claimant Work Address not set or is null
        // When report data is requested
        // Then on all cases with valid date "Null" should be used for postcode in the report data detail entries
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
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), START_DATE, END_DATE))
                .thenReturn(submitEvents);

        var expectedClaimantAddressUKPostcode = "DH3 8HL";
        var expectedClaimantWorkPostcode = "Null";
        var expectedRespondentPostcode = "Null";
        var expectedRespondentET3Postcode = "Null";

        var reportData = claimsByHearingVenueReport
                .generateReport(LEEDS_LISTING_CASE_TYPE_ID, null);

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
    void shouldShowNullStringValueForMissingPostCodeInReportDetailEntry() {
        // Given a case has a Claimant Work Address provided and postcode in it not set or is null
        // When report data is requested
        // Then on all cases with valid date "Null" should be used for postcode in the report data detail entries
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
            UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), START_DATE, END_DATE))
            .thenReturn(submitEvents);

        var expectedClaimantAddressUKPostcode = "Null";
        var expectedClaimantWorkPostcode = "Null";
        var expectedRespondentPostcode = "Null";
        var expectedRespondentET3Postcode = "Null";

        var reportData = claimsByHearingVenueReport
            .generateReport(LEEDS_LISTING_CASE_TYPE_ID, null);

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
         // no postcode found

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
             UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), START_DATE, END_DATE))
             .thenReturn(submitEvents);

         var expectedReportDetailEntriesCount = submitEvents.size();
         var expectedRespondentPostCode = firstRespondentAddress.getPostCode();
         var expectedFirstRespondentET3AddressPostCode = firstRespondentET3Address.getPostCode();
         var listingDetails = new ListingDetails();
         var reportData = claimsByHearingVenueReport
             .generateReport(LEEDS_LISTING_CASE_TYPE_ID, null);
         var actualRespondentPostCode = reportData.getReportDetails().get(0).getRespondentPostcode();
         var actualRespondentET3PostCode = reportData.getReportDetails().get(0).getRespondentET3Postcode();

         assertEquals(expectedReportDetailEntriesCount, reportData.getReportDetails().size());
         assertEquals(expectedRespondentPostCode, actualRespondentPostCode);
         assertEquals(expectedFirstRespondentET3AddressPostCode, actualRespondentET3PostCode);
     }

    @Test
    void shouldShowReportDetailEntriesSortedByEthosCaseReference() {
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
            UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID), START_DATE, END_DATE))
            .thenReturn(submitEvents);

        var expectedNumberOfSubmitEventEntries = submitEvents.size();
        var expectedFirstCaseReference = submitEvents.get(1).getCaseData().getEthosCaseReference();
        var expectedSecondCaseReference = submitEvents.get(2).getCaseData().getEthosCaseReference();
        var expectedThirdFirstCaseReference = submitEvents.get(0).getCaseData().getEthosCaseReference();
        var reportData = claimsByHearingVenueReport
            .generateReport(LEEDS_LISTING_CASE_TYPE_ID, null);

        assertEquals(expectedNumberOfSubmitEventEntries, reportData.getReportDetails().size());
        assertEquals(expectedFirstCaseReference, reportData.getReportDetails().get(0).getCaseReference());
        assertEquals(expectedSecondCaseReference, reportData.getReportDetails().get(1).getCaseReference());
        assertEquals(expectedThirdFirstCaseReference, reportData.getReportDetails().get(2).getCaseReference());
    }
}
