package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.listing.ListingRequest;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelperTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_LISTED_CASE_ERROR;
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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TARGET_HEARING_DATE_INCREMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.CaseCloseValidator.CLOSING_CASE_WITH_BF_OPEN_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService.RECEIPT_DATE_LATER_THAN_REJECTED_ERROR_MESSAGE;

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
    void setup() throws Exception {
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
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        List<String> errors = eventValidationService.validateReceiptDate(caseDetails);

        assertEquals(0, errors.size());
        assertEquals(caseData.getTargetHearingDate(), PAST_TARGET_HEARING_DATE.toString());
    }

    @ParameterizedTest
    @CsvSource({"2023-01-01", "2019-01-01"})
    void shouldValidateRejectedDate(String rejectedDate) {
        caseData.setReceiptDate("2022-01-01");
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setDateRejected(rejectedDate);
        caseData.setPreAcceptCase(casePreAcceptType);
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setState(REJECTED_STATE);
        caseDetails.setCaseData(caseData);
        List<String> errors = eventValidationService.validateReceiptDate(caseDetails);
        if (rejectedDate.equals("2023-01-01")) {
            assertEquals(0, errors.size());
        }
        if (rejectedDate.equals("2019-01-01")) {
            assertEquals(RECEIPT_DATE_LATER_THAN_REJECTED_ERROR_MESSAGE, errors.getFirst());
        }
    }
    
    @Test
    void shouldValidateCurrentReceiptDate() {
        caseData.setReceiptDate(CURRENT_RECEIPT_DATE.toString());
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setState(ACCEPTED_STATE);
        caseDetails.setCaseData(caseData);
        List<String> errors = eventValidationService.validateReceiptDate(caseDetails);

        assertEquals(0, errors.size());
        assertEquals(caseData.getTargetHearingDate(), CURRENT_TARGET_HEARING_DATE.toString());
    }

    @Test
    void shouldValidateFutureReceiptDate() {
        caseData.setReceiptDate(FUTURE_RECEIPT_DATE.toString());
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        List<String> errors = eventValidationService.validateReceiptDate(caseDetails);

        assertEquals(1, errors.size());
        assertEquals(FUTURE_RECEIPT_DATE_ERROR_MESSAGE, errors.getFirst());
    }

    @ParameterizedTest
    @CsvSource({
        MULTIPLE_CASE_TYPE + "," + SUBMITTED_STATE,
        MULTIPLE_CASE_TYPE + "," + ACCEPTED_STATE,
        SINGLE_CASE_TYPE + "," + SUBMITTED_STATE,
        SINGLE_CASE_TYPE + "," + ACCEPTED_STATE
    })
    void shouldValidateCaseState(String caseType, String caseState) {
        caseDetails1.getCaseData().setEcmCaseType(caseType);
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
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setState(ACCEPTED_STATE);
        caseDetails.setCaseData(caseData);
        List<String> errors = eventValidationService.validateReceiptDate(caseDetails);

        assertEquals(1, errors.size());
        assertEquals(RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE, errors.getFirst());
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
        assertEquals(FUTURE_RECEIPT_DATE_ERROR_MESSAGE, errors.getFirst());
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
        assertEquals(EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE, errors.getFirst());
    }

    @Test
    void shouldValidateReturnedFromJudgeDateBeforeReferredToJudgeDate() {
        List<String> errors = eventValidationService.validateET3ResponseFields(caseDetails1.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE
                + " for respondent 1 (Antonio Vazquez)", errors.getFirst());
    }

    @Test
    void shouldValidateReturnedFromJudgeDateAndReferredToJudgeDateAreMissingDate() {
        List<String> errors = eventValidationService.validateET3ResponseFields(caseDetails3.getCaseData());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateResponseReceivedDateIsFutureDate() {
        caseData = caseDetails1.getCaseData();

        caseData.getRespondentCollection().getFirst().getValue()
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
        caseData = caseDetails3.getCaseData();

        List<String> errors = eventValidationService.validateET3ResponseFields(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithEmptyRepCollection() {
        caseData = caseDetails1.getCaseData();

        List<String> errors = eventValidationService.validateAndSetRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithMismatch() {
        caseData = caseDetails2.getCaseData();

        List<String> errors = eventValidationService.validateAndSetRespRepNames(caseData);

        assertEquals(1, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithMatch() {
        caseData = caseDetails3.getCaseData();

        List<String> errors = eventValidationService.validateAndSetRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithNullRepCollection() {
        caseData = caseDetails4.getCaseData();

        List<String> errors = eventValidationService.validateAndSetRespRepNames(caseData);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateRespRepNamesWithMatchResponseName() {
        caseData = caseDetails5.getCaseData();

        List<String> errors = eventValidationService.validateAndSetRespRepNames(caseData);

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
        assertEquals(HEARING_NUMBER_MISMATCH_ERROR_MESSAGE, errors.getFirst());
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
        assertEquals(EMPTY_HEARING_COLLECTION_ERROR_MESSAGE, errors.getFirst());
    }

    @Test
    void shouldValidateJurisdictionCodesWithDuplicatesCodesAndExistenceJudgement() {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionCodes(caseDetails1.getCaseData(), errors);

        assertEquals(2, errors.size());
        assertEquals(DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE + " \"COM\" in Jurisdiction 3 "
                + "- \"DOD\" in Jurisdiction 5 ", errors.getFirst());
        assertEquals(JURISDICTION_CODES_DELETED_ERROR + "[CCP, ADG]", errors.get(1));
    }

    @Test
    void shouldValidateJurisdictionCodesWithUniqueCodes() {
        List<String> errors = new ArrayList<>();
        eventValidationService.validateJurisdictionCodes(caseDetails2.getCaseData(), errors);

        assertEquals(0, errors.size());
    }

    @ParameterizedTest
    @CsvSource({SUBMITTED_STATE + ",false", ACCEPTED_STATE + ",false",
        REJECTED_STATE + ",false", CLOSED_STATE + ",true"})
    void validateCurrentPositionCaseClosed(String state, boolean expected) {
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setState(state);
        caseData = new CaseData();
        caseData.setPositionType(CASE_CLOSED_POSITION);
        caseDetails.setCaseData(caseData);
        boolean validated = eventValidationService.validateCurrentPosition(caseDetails);
        assertEquals(expected, validated);
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
                    + MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE, errors.getFirst());
        } else {
            assertEquals(MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE, errors.getFirst());
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
                    + JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE, errors.getFirst());
        } else {
            assertEquals(JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE, errors.getFirst());
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
                        + MISSING_JURISDICTION_MESSAGE, errors.getFirst());
            } else {
                assertEquals(MISSING_JURISDICTION_MESSAGE, errors.getFirst());
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
                    + MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.getFirst());
        } else {
            assertEquals(MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.getFirst());
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
                    + MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.getFirst());
        } else {
            assertEquals(MISSING_JUDGEMENT_JURISDICTION_MESSAGE, errors.getFirst());
        }
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void shouldValidateCaseBeforeCloseEventWithErrors(boolean isRejected, boolean partOfMultiple) {
        caseData = caseDetails18.getCaseData();
        caseData.setBfActions(BFHelperTest.generateBFActionTypeItems());
        caseData.getBfActions().getFirst().getValue().setCleared(null);
        List<String> errors = new ArrayList<>();
        eventValidationService.validateCaseBeforeCloseEvent(caseData, isRejected, partOfMultiple, errors);

        assertEquals(4, errors.size());
        assertThat(errors).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(String.format(CLOSING_CASE_WITH_BF_OPEN_ERROR, caseData.getEthosCaseReference()));
        if (partOfMultiple) {
            assertThat(errors).asInstanceOf(InstanceOfAssertFactories.LIST)
                .contains(caseData.getEthosCaseReference() + " - " + MISSING_JUDGEMENT_JURISDICTION_MESSAGE);
            assertThat(errors).asInstanceOf(InstanceOfAssertFactories.LIST)
                .doesNotContain(caseData.getEthosCaseReference() + " - " + CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR);
        } else {
            assertThat(errors).asInstanceOf(InstanceOfAssertFactories.LIST)
                .contains(MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE);
            assertThat(errors).asInstanceOf(InstanceOfAssertFactories.LIST)
                .doesNotContain(CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR);
            assertThat(errors).asInstanceOf(InstanceOfAssertFactories.LIST)
                .contains(CLOSING_LISTED_CASE_ERROR);
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
        assertEquals(JURISDICTION_CODES_EXISTENCE_ERROR + "ADG, ADG, ADG, CCP, CCP", errors.getFirst());
        assertEquals(DUPLICATED_JURISDICTION_CODES_JUDGEMENT_ERROR + "Case Management - [COM] & Reserved "
                + "- [CCP, ADG]", errors.get(1));
    }

    @Test
    void shouldCreateErrorMessageWithDatesInFutureWithinJudgement() {
        caseData = new CaseData();
        var judgementTypeItem = new JudgementTypeItem();
        var judgementType = new JudgementType();
        judgementTypeItem.setId(UUID.randomUUID().toString());
        judgementType.setDateJudgmentMade("2777-01-01");
        judgementType.setDateJudgmentSent("2777-01-01");
        judgementTypeItem.setValue(judgementType);
        caseData.setJudgementCollection(List.of(judgementTypeItem));
        List<String> errors = eventValidationService.validateJudgementDates(caseData);
        assertEquals("Date of Judgement Made can't be in future", errors.getFirst());
        assertEquals("Date of Judgement Sent can't be in future", errors.get(1));
    }

    @Test
    void shouldNotCreateErrorMessageWithDatesBeforeTodayWithinJudgement() {
        caseData = new CaseData();
        var judgementTypeItem = new JudgementTypeItem();
        var judgementType = new JudgementType();
        judgementTypeItem.setId(UUID.randomUUID().toString());
        judgementType.setDateJudgmentMade("2020-01-01");
        judgementType.setDateJudgmentSent("2021-12-01");
        judgementTypeItem.setValue(judgementType);
        caseData.setJudgementCollection(List.of(judgementTypeItem));
        List<String> errors = eventValidationService.validateJudgementDates(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateJurisdictionCodesWithinJudgementEmptyJurCodesCollection() {
        List<String> errors = eventValidationService.validateJurisdictionCodesWithinJudgement(
                caseDetails3.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(JURISDICTION_CODES_EXISTENCE_ERROR + "ADG, COM", errors.getFirst());
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
        assertEquals(INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE, errors.getFirst());
    }

    @Test
    void shouldValidateReportDateRangeInvalidDates_31Days() {

        var listingsCase = listingRequest31DaysInvalidRange.getCaseDetails().getCaseData();
        var errors = eventValidationService.validateListingDateRange(
                listingsCase.getListingDateFrom(),
                listingsCase.getListingDateTo()
        );
        assertEquals(1, errors.size());
        assertEquals(INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE, errors.getFirst());
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

    @Test
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
        assertEquals(CLOSING_LISTED_CASE_ERROR, errors.getFirst());
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
        assertEquals(CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR, errors.getFirst());
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
