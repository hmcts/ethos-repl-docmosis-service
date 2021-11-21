package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.listing.ListingRequest;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_LISTED_CASE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATED_JURISDICTION_CODES_JUDGEMENT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EMPTY_HEARING_COLLECTION_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FUTURE_RECEIPT_DATE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FUTURE_RESPONSE_RECEIVED_DATE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_NUMBER_MISMATCH_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_DELETED_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_EXISTENCE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JUDGEMENT_JURISDICTION_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JURISDICTION_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TARGET_HEARING_DATE_INCREMENT;

@ExtendWith(SpringExtension.class)
class EventValidationServiceTest {

    private static final LocalDate PAST_RECEIPT_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate CURRENT_RECEIPT_DATE = LocalDate.now();
    private static final LocalDate FUTURE_RECEIPT_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate PAST_ACCEPTED_DATE = LocalDate.now().minusDays(1);

    private static final LocalDate PAST_TARGET_HEARING_DATE = PAST_RECEIPT_DATE.plusDays(TARGET_HEARING_DATE_INCREMENT);
    private static final LocalDate CURRENT_TARGET_HEARING_DATE =
            CURRENT_RECEIPT_DATE.plusDays(TARGET_HEARING_DATE_INCREMENT);

    private static final LocalDate PAST_RESPONSE_RECEIVED_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate CURRENT_RESPONSE_RECEIVED_DATE = LocalDate.now();
    private static final LocalDate FUTURE_RESPONSE_RECEIVED_DATE = LocalDate.now().plusDays(1);

    private EventValidationService eventValidationService;

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails3;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetails5;
    private CaseDetails validHearingStatusCaseCloseEventCaseDetails;
    private CaseDetails invalidHearingStatusCaseCloseEventCaseDetails;
    private CaseDetails validJudgeAllocationCaseDetails;
    private CaseDetails invalidJudgeAllocationCaseDetails;
    private CaseDetails outcomeNotAllocatedCaseDetails;
    private CaseDetails caseDetails16;
    private CaseDetails caseDetails17;
    private CaseDetails caseDetails18;
    private ListingRequest listingRequestValidDateRange;
    private ListingRequest listingRequestInvalidDateRange;
    private ListingRequest listingRequest31DaysInvalidRange;
    private ListingRequest listingRequest30DaysValidRange;

    private CaseData caseData;
    private MultipleData multipleData;

    @BeforeEach
    public void setup() throws Exception {
        eventValidationService = new EventValidationService();

        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails3 = generateCaseDetails("caseDetailsTest3.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails5 = generateCaseDetails("caseDetailsTest5.json");
        validHearingStatusCaseCloseEventCaseDetails = generateCaseDetails(
                "CaseCloseEvent_ValidHearingStatusCaseDetails.json");
        invalidHearingStatusCaseCloseEventCaseDetails = generateCaseDetails(
                "CaseCloseEvent_InValidHearingStatusCaseDetails.json");
        validJudgeAllocationCaseDetails = generateCaseDetails(
                "CaseCloseEvent_ValidJudgeAllocationStatusCaseDetails.json");
        invalidJudgeAllocationCaseDetails = generateCaseDetails(
                "CaseCloseEvent_InValidJudgeAllocationStatusCaseDetails.json");
        outcomeNotAllocatedCaseDetails = generateCaseDetails(
                "CaseCloseEvent_JurisdictionOutcomeNotAllocated.json");
        caseDetails16 = generateCaseDetails("caseDetailsTest16.json");
        caseDetails17 = generateCaseDetails("caseDetailsTest17.json");
        caseDetails18 = generateCaseDetails("caseDetailsTest18.json");

        listingRequestValidDateRange = generateListingDetails("exampleListingV1.json");
        listingRequestInvalidDateRange = generateListingDetails("exampleListingV3.json");
        listingRequest31DaysInvalidRange = generateListingDetails("exampleListingV5.json");
        listingRequest30DaysValidRange = generateListingDetails("exampleListingV4.json");

        caseData = new CaseData();
        multipleData = new MultipleData();
    }

    @Test
    void shouldValidatePastReceiptDate() {
        caseData.setReceiptDate(PAST_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(0, errors.size());
        assertEquals(caseData.getTargetHearingDate(), PAST_TARGET_HEARING_DATE.toString());
    }

    @Test
    void shouldValidateCurrentReceiptDate() {
        caseData.setReceiptDate(CURRENT_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(0, errors.size());
        assertEquals(caseData.getTargetHearingDate(), CURRENT_TARGET_HEARING_DATE.toString());
    }

    @Test
    void shouldValidateFutureReceiptDate() {
        caseData.setReceiptDate(FUTURE_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(1, errors.size());
        assertEquals(FUTURE_RECEIPT_DATE_ERROR_MESSAGE, errors.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        MULTIPLE_CASE_TYPE + "," + SUBMITTED_STATE,
        MULTIPLE_CASE_TYPE + "," + ACCEPTED_STATE,
        SINGLE_CASE_TYPE + "," + SUBMITTED_STATE,
        SINGLE_CASE_TYPE + "," + ACCEPTED_STATE
    })
    void shouldValidateCaseState(String caseType, String caseState) {
        caseDetails1.getCaseData().setCaseType(caseType);
        caseDetails1.setState(caseState);

        boolean validated = eventValidationService.validateCaseState(caseDetails1);

        if (Objects.equals(caseType, MULTIPLE_CASE_TYPE) && Objects.equals(caseState, SUBMITTED_STATE)) {
            assertFalse(validated);
        } else {
            assertTrue(validated);
        }
    }

    @Test
    void shouldValidateReceiptDateLaterThanAcceptedDate() {
        caseData.setReceiptDate(CURRENT_RECEIPT_DATE.toString());
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setDateAccepted(PAST_ACCEPTED_DATE.toString());
        caseData.setPreAcceptCase(casePreAcceptType);

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(1, errors.size());
        assertEquals(RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    void shouldValidatePastReceiptDateMultiple() {
        multipleData.setReceiptDate(PAST_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDateMultiple(multipleData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateFutureReceiptDateMultiple() {
        multipleData.setReceiptDate(FUTURE_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDateMultiple(multipleData);

        assertEquals(1, errors.size());
        assertEquals(FUTURE_RECEIPT_DATE_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    void shouldValidateActiveRespondentsAllFound() {
        List<String> errors = eventValidationService.validateActiveRespondents(caseDetails1.getCaseData());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateActiveRespondentsNoneFound() {
        List<String> errors = eventValidationService.validateActiveRespondents(caseDetails2.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    void shouldValidateReturnedFromJudgeDateBeforeReferredToJudgeDate() {
        List<String> errors = eventValidationService.validateET3ResponseFields(caseDetails1.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE
                + " for respondent 1 (Antonio Vazquez)", errors.get(0));
    }

    @Test
    void shouldValidateReturnedFromJudgeDateAndReferredToJudgeDateAreMissingDate() {
        List<String> errors = eventValidationService.validateET3ResponseFields(caseDetails3.getCaseData());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateResponseReceivedDateIsFutureDate() {
        CaseData caseData = caseDetails1.getCaseData();

        caseData.getRespondentCollection().get(0).getValue()
                .setResponseReceivedDate(PAST_RESPONSE_RECEIVED_DATE.toString());
        caseData.getRespondentCollection().get(1).getValue()
                .setResponseReceivedDate(CURRENT_RESPONSE_RECEIVED_DATE.toString());
        caseData.getRespondentCollection().get(2).getValue()
                .setResponseReceivedDate(FUTURE_RESPONSE_RECEIVED_DATE.toString());

        List<String> errors = eventValidationService.validateET3ResponseFields(caseData);

        assertEquals(2, errors.size());
        assertEquals(FUTURE_RESPONSE_RECEIVED_DATE_ERROR_MESSAGE
                + " for respondent 3 (Mike Jordan)", errors.get(1));
    }

    @Test
    void shouldValidateResponseReceivedDateForMissingDate() {
        CaseData caseData = caseDetails3.getCaseData();

        List<String> errors = eventValidationService.validateET3ResponseFields(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithEmptyRepCollection() {
        CaseData caseData = caseDetails1.getCaseData();

        List<String> errors = eventValidationService.validateRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithMismatch() {
        CaseData caseData = caseDetails2.getCaseData();

        List<String> errors = eventValidationService.validateRespRepNames(caseData);

        assertEquals(1, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithMatch() {
        CaseData caseData = caseDetails3.getCaseData();

        List<String> errors = eventValidationService.validateRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithNullRepCollection() {
        CaseData caseData = caseDetails4.getCaseData();

        List<String> errors = eventValidationService.validateRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithMatchResponseName() {
        CaseData caseData = caseDetails5.getCaseData();

        List<String> errors = eventValidationService.validateRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateHearingNumberMatching() {
        List<String> errors = eventValidationService.validateHearingNumber(caseDetails1.getCaseData(),
                caseDetails1.getCaseData().getCorrespondenceType(),
                caseDetails1.getCaseData().getCorrespondenceScotType());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateHearingNumberMismatch() {
        List<String> errors = eventValidationService.validateHearingNumber(caseDetails2.getCaseData(),
                caseDetails2.getCaseData().getCorrespondenceType(),
                caseDetails2.getCaseData().getCorrespondenceScotType());

        assertEquals(1, errors.size());
        assertEquals(HEARING_NUMBER_MISMATCH_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    void shouldValidateHearingNumberMissing() {
        List<String> errors = eventValidationService.validateHearingNumber(caseDetails3.getCaseData(),
                caseDetails3.getCaseData().getCorrespondenceType(),
                caseDetails3.getCaseData().getCorrespondenceScotType());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateHearingNumberForEmptyHearings() {
        List<String> errors = eventValidationService.validateHearingNumber(caseDetails4.getCaseData(),
                caseDetails4.getCaseData().getCorrespondenceType(),
                caseDetails4.getCaseData().getCorrespondenceScotType());

        assertEquals(1, errors.size());
        assertEquals(EMPTY_HEARING_COLLECTION_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    void shouldValidateJurisdictionCodesWithDuplicatesCodesAndExistenceJudgement() {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionCodes(caseDetails1.getCaseData(), errors);

        assertEquals(2, errors.size());
        assertEquals(DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE + " \"COM\" in Jurisdiction 3 "
                + "- \"DOD\" in Jurisdiction 5 ", errors.get(0));
        assertEquals(JURISDICTION_CODES_DELETED_ERROR + "[CCP, ADG]", errors.get(1));
    }

    @Test
    void shouldValidateJurisdictionCodesWithUniqueCodes() {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionCodes(caseDetails2.getCaseData(), errors);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateJurisdictionCodesWithEmptyCodes() {
        List<String> errors = new ArrayList<>();
        caseDetails3.getCaseData().setJudgementCollection(new ArrayList<>());
        eventValidationService.validateJurisdictionCodes(caseDetails3.getCaseData(), errors);

        assertEquals(0, errors.size());
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateJurisdictionOutcomePresentAndMissing(boolean isRejected, boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionOutcome(caseDetails1.getCaseData(), isRejected,
                partOfMultiple, errors);

        assertEquals(1, errors.size());
        if (partOfMultiple) {
            assertEquals(caseDetails1.getCaseData().getEthosCaseReference() + " - "
                    + MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE, errors.get(0));
        } else {
            assertEquals(MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE, errors.get(0));
        }
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateWhenJurisdictionOutcomeSetToNotAllocated(boolean isRejected, boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionOutcome(outcomeNotAllocatedCaseDetails.getCaseData(),
                isRejected, partOfMultiple, errors);

        assertEquals(1, errors.size());
        if (partOfMultiple) {
            assertEquals(outcomeNotAllocatedCaseDetails.getCaseData().getEthosCaseReference() + " - "
                    + JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE, errors.get(0));
        } else {
            assertEquals(JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE, errors.get(0));
        }
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateJurisdictionOutcomePresent(boolean isRejected, boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionOutcome(caseDetails2.getCaseData(),
                isRejected, partOfMultiple, errors);

        assertEquals(0, errors.size());
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateJurisdictionOutcomeMissing(boolean isRejected, boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionOutcome(caseDetails3.getCaseData(),
                isRejected, partOfMultiple, errors);

        if (isRejected) {
            assertEquals(0, errors.size());
        } else {
            assertEquals(1, errors.size());

            if (partOfMultiple) {
                assertEquals(caseDetails1.getCaseData().getEthosCaseReference() + " - "
                        + MISSING_JURISDICTION_MESSAGE, errors.get(0));
            } else {
                assertEquals(MISSING_JURISDICTION_MESSAGE, errors.get(0));
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldValidateJurisdictionCodeForJudgementPresentAndMissing(boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJudgementsHasJurisdiction(caseDetails18.getCaseData(), partOfMultiple, errors);

        assertEquals(1, errors.size());
        if (partOfMultiple) {
            assertEquals(caseDetails18.getCaseData().getEthosCaseReference() + " - "
                    + MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.get(0));
        } else {
            assertEquals(MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.get(0));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldValidateJurisdictionCodeForJudgementPresent(boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJudgementsHasJurisdiction(caseDetails17.getCaseData(), partOfMultiple, errors);

        assertEquals(0, errors.size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldValidateJurisdictionCodeForJudgementMissing(boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJudgementsHasJurisdiction(caseDetails16.getCaseData(), partOfMultiple, errors);

        assertEquals(1, errors.size());
        if (partOfMultiple) {
            assertEquals(caseDetails16.getCaseData().getEthosCaseReference() + " - "
                    + MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.get(0));
        } else {
            assertEquals(MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.get(0));
        }
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateCaseBeforeCloseEventWithErrors(boolean isRejected, boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateCaseBeforeCloseEvent(caseDetails18.getCaseData(),
                isRejected, partOfMultiple, errors);

        assertEquals(3, errors.size());
        if (partOfMultiple) {
            assertThat(errors).asList().contains(caseDetails18.getCaseData().getEthosCaseReference()
                    + " - " + MISSING_JUDGEMENT_JURISDICTION_MESSAGE);
            assertThat(errors).asList().doesNotContain(caseDetails18.getCaseData().getEthosCaseReference()
                    + " - " + CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR);
        } else {
            assertThat(errors).asList().contains(MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE);
            assertThat(errors).asList().doesNotContain(CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR);
            assertThat(errors).asList().contains(CLOSING_LISTED_CASE_ERROR);
        }
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateCaseBeforeCloseEventNoErrors(boolean isRejected, boolean partOfMultiple) {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateCaseBeforeCloseEvent(caseDetails17.getCaseData(),
                isRejected, partOfMultiple, errors);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateJurisdictionCodesWithinJudgement() {
        List<String> errors = eventValidationService.validateJurisdictionCodesWithinJudgement(
                caseDetails1.getCaseData());

        assertEquals(2, errors.size());
        assertEquals(JURISDICTION_CODES_EXISTENCE_ERROR + "ADG, ADG, ADG, CCP, CCP", errors.get(0));
        assertEquals(DUPLICATED_JURISDICTION_CODES_JUDGEMENT_ERROR + "Case Management - [COM] & Reserved "
                + "- [CCP, ADG]", errors.get(1));
    }

    @Test
    void shouldValidateJurisdictionCodesWithinJudgementEmptyJurCodesCollection() {
        List<String> errors = eventValidationService.validateJurisdictionCodesWithinJudgement(
                caseDetails3.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(JURISDICTION_CODES_EXISTENCE_ERROR + "ADG, COM", errors.get(0));
    }

    @Test
    void shouldValidateDepositRefunded() {
        List<String> errors = eventValidationService.validateDepositRefunded(caseDetails3.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR, errors.get(0));
    }

    @Test
    void shouldValidateNullDepositRefunded() {
        List<String> errors = eventValidationService.validateDepositRefunded(caseDetails2.getCaseData());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateDepositRefundedWithNullAmount() {
        List<String> errors = eventValidationService.validateDepositRefunded(caseDetails1.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR, errors.get(0));
    }

    @Test
    void shouldValidateReportDateRangeValidDates() {

        var listingsCase = listingRequestValidDateRange.getCaseDetails().getCaseData();
        var errors = eventValidationService.validateListingDateRange(
                listingsCase.getListingDateFrom(),
                listingsCase.getListingDateTo()
        );
        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateReportDateRangeValidDates_30Days() {

        var listingsCase = listingRequest30DaysValidRange.getCaseDetails().getCaseData();
        var errors = eventValidationService.validateListingDateRange(
                listingsCase.getListingDateFrom(),
                listingsCase.getListingDateTo()
        );
        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateReportDateRangeInvalidDates() {

        var listingsCase = listingRequestInvalidDateRange.getCaseDetails().getCaseData();
        var errors = eventValidationService.validateListingDateRange(
                listingsCase.getListingDateFrom(),
                listingsCase.getListingDateTo()
        );
        assertEquals(1, errors.size());
        assertEquals(INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE, errors.get(0));
    }

    @Test
    void shouldValidateReportDateRangeInvalidDates_31Days() {

        var listingsCase = listingRequest31DaysInvalidRange.getCaseDetails().getCaseData();
        var errors = eventValidationService.validateListingDateRange(
                listingsCase.getListingDateFrom(),
                listingsCase.getListingDateTo()
        );
        assertEquals(1, errors.size());
        assertEquals(INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE, errors.get(0));
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    private ListingRequest generateListingDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ListingRequest.class);
    }

    @Test
    void validateRestrictedBy() {
        eventValidationService.validateRestrictedReportingNames(caseDetails2.getCaseData());
        assertEquals("Claimant", caseDetails2.getCaseData().getRestrictedReporting().getRequestedBy());
        eventValidationService.validateRestrictedReportingNames(caseDetails1.getCaseData());
        assertEquals("Judge", caseDetails1.getCaseData().getRestrictedReporting().getRequestedBy());
        eventValidationService.validateRestrictedReportingNames(caseDetails3.getCaseData());
        assertEquals("Respondent", caseDetails3.getCaseData().getRestrictedReporting().getRequestedBy());
    }

    void shouldReturnsNoErrorsForHearingHearingStatusValidationWithNoHearings() {
        List<String> errors = new ArrayList<>();
        var caseWithNoHearings = validHearingStatusCaseCloseEventCaseDetails.getCaseData();
        caseWithNoHearings.getHearingCollection().clear();
        eventValidationService.validateHearingStatusForCaseCloseEvent(caseWithNoHearings, errors);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldPassCaseCloseEventValidationCaseWithNoListedHearingStatus() {
        List<String> errors = new ArrayList<>();
        var validCase = validHearingStatusCaseCloseEventCaseDetails.getCaseData();
        eventValidationService.validateHearingStatusForCaseCloseEvent(validCase, errors);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldFailCaseCloseEventValidationCaseWithListedHearingStatus() {
        List<String> errors = new ArrayList<>();
        var invalidCase = invalidHearingStatusCaseCloseEventCaseDetails.getCaseData();
        eventValidationService.validateHearingStatusForCaseCloseEvent(invalidCase, errors);
        assertEquals(1, errors.size());
        assertEquals(CLOSING_LISTED_CASE_ERROR, errors.get(0));
    }

    @Test
    void shouldPassHearingJudgeAllocationValidationForCaseCloseEventHearingWithJudge() {
        List<String> errors = new ArrayList<>();
        var validCase = validJudgeAllocationCaseDetails.getCaseData();
        eventValidationService.validateHearingJudgeAllocationForCaseCloseEvent(validCase, errors);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldFailHearingJudgeAllocationValidationForCaseCloseEventHearingWithNoJudge() {
        List<String> errors = new ArrayList<>();
        var invalidCase = invalidJudgeAllocationCaseDetails.getCaseData();
        eventValidationService.validateHearingJudgeAllocationForCaseCloseEvent(invalidCase, errors);
        assertEquals(1, errors.size());
        assertEquals(CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR, errors.get(0));
    }

    @Test
    void shouldReturnsNoErrorsForHearingJudgeAllocationValidationWithNoHearings() {
        List<String> errors = new ArrayList<>();
        var caseWithNoHearings = invalidJudgeAllocationCaseDetails.getCaseData();
        caseWithNoHearings.getHearingCollection().clear();
        eventValidationService.validateHearingJudgeAllocationForCaseCloseEvent(caseWithNoHearings, errors);
        assertEquals(0, errors.size());
    }

}
