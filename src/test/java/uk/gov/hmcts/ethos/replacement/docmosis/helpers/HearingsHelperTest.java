package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.helper.Constants;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.HEARING_STATUS_SETTLED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.HEARING_BREAK_FUTURE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.HEARING_BREAK_RESUME_INVALID;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.HEARING_FINISH_FUTURE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.HEARING_FINISH_INVALID;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.HEARING_RESUME_FUTURE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.HEARING_START_FUTURE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.TWO_JUDGES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.TWO_JUDGES_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.findHearingNumber;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.hearingMidEventValidation;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.hearingTimeValidation;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.isDateInFuture;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.setHearingDaysAndDates;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_DAY_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_NUMBER_ERROR;

@Slf4j
@ExtendWith(SpringExtension.class)
class HearingsHelperTest {

    private CaseData caseData;

    @BeforeEach
    void setUp() throws Exception {
        caseData = generateCaseDetails().getCaseData();
    }

    private CaseDetails generateCaseDetails() throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("caseDetailsTestHearingDetailsUpdate.json")).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    void hearingMidEventValidationNumberError() {

        caseData.getHearingCollection().getFirst().getValue().setHearingNumber(null);

        assertEquals(1, hearingMidEventValidation(caseData).size());

        assertEquals(HEARING_CREATION_NUMBER_ERROR,
            hearingMidEventValidation(caseData).getFirst());

        caseData.getHearingCollection().getFirst().getValue().setHearingNumber("");

        assertEquals(1, hearingMidEventValidation(caseData).size());

        assertEquals(HEARING_CREATION_NUMBER_ERROR,
            hearingMidEventValidation(caseData).getFirst());

    }

    @Test
    void hearingMidEventValidationDayError() {

        caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setListedDate(null);

        assertEquals(1, hearingMidEventValidation(caseData).size());

        assertEquals(HEARING_CREATION_DAY_ERROR, hearingMidEventValidation(caseData).getFirst());

        caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setListedDate("");

        assertEquals(1, hearingMidEventValidation(caseData).size());

        assertEquals(HEARING_CREATION_DAY_ERROR, hearingMidEventValidation(caseData).getFirst());

        caseData.getHearingCollection().getFirst().getValue()
            .setHearingDateCollection(null);

        assertEquals(0, hearingMidEventValidation(caseData).size());

    }

    @Test
    void updatePostponedDate() {

        caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_POSTPONED);

        HearingsHelper.updatePostponedDate(caseData);

        assertNotNull(caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().getPostponedDate());

        caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_SETTLED);

        HearingsHelper.updatePostponedDate(caseData);

        assertNull(caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().getPostponedDate());
    }

    @Test
    void findDateOfHearingTest() {
        var hearingDate = caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().getListedDate().substring(0, 10);
        var hearingNumber = findHearingNumber(caseData, hearingDate);
        assertEquals(hearingNumber, caseData.getHearingCollection().getFirst().getValue().getHearingNumber());
    }

    @Test
    void findDateOfHearing_DateNotInHearing() {
        assertNull(findHearingNumber(caseData, "1970-10-01"));
    }

    @Test
    void validateStartFinishTime_validTime() {
        setValidHearingStartFinishTimes();
        caseData.getHearingCollection().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    void validateBreakResumeTime_invalidBreak() {
        setValidHearingStartFinishTimes();
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingBreak("2019-11-01T00:00:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);

        List<String> errors = hearingTimeValidation(caseData);
        var hearingNumber = caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingNumber();
        assertEquals(1, errors.size());
        assertEquals(String.format(HEARING_BREAK_RESUME_INVALID, hearingNumber), errors.getFirst());
    }

    @Test
    void validateBreakResumeTime_invalidResume() {
        setValidHearingStartFinishTimes();
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingResume("2019-11-01T00:00:00.000");

        List<String> errors = hearingTimeValidation(caseData);
        var hearingNumber = caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingNumber();
        assertEquals(1, errors.size());
        assertEquals(String.format(HEARING_BREAK_RESUME_INVALID, hearingNumber), errors.getFirst());
    }

    @Test
    void validateBreakResumeTime_nullBreakResume() {
        setValidHearingStartFinishTimes();
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingBreak(null);
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingResume(null);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    void validateStartFinishTime_sameTime() {
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingStart("2019-11-01T12:11:00.000");
        // Same time as start time
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingFinish("2019-11-01T12:11:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        var hearingNumber = caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingNumber();

        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(1, errors.size());
        assertEquals(HEARING_FINISH_INVALID + hearingNumber, errors.getFirst());
    }

    @Test
    void validateStartFinishTime_finishTimeBeforeStart() {
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingStart("2019-11-01T12:11:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingFinish("2019-11-01T12:10:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        var hearingNumber = caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingNumber();

        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(1, errors.size());
        assertEquals(HEARING_FINISH_INVALID + hearingNumber, errors.getFirst());
    }

    @Test
    void validateHearingDatesInPastTest() {
        caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingDateCollection()
            .getFirst().getValue().setHearingTimingBreak("2021-12-19T10:00:00");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingDateCollection()
            .getFirst().getValue().setHearingTimingResume("2021-12-19T10:00:00");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingDateCollection()
            .getFirst().getValue().setHearingTimingFinish("2021-12-19T10:10:00");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingDateCollection()
            .getFirst().getValue().setHearingTimingStart("2021-12-19T10:00:00");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    void validateIsDateInFutureConsideringDST() {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(25);
        LocalDateTime now = LocalDateTime.now(UTC);
        boolean val = isDateInFuture(dateTime.toString(), now);
        assertFalse(val);
    }

    @Test
    void invalidateHearingDatesInFutureTest() {
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingStart("2222-11-01T12:11:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingFinish("2222-11-01T12:11:20.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingResume("2222-11-01T12:11:20.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingBreak("2222-11-01T12:11:20.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(4, errors.size());
        assertTrue(errors.contains(HEARING_START_FUTURE));
        assertTrue(errors.contains(HEARING_FINISH_FUTURE));
        assertTrue(errors.contains(HEARING_BREAK_FUTURE));
        assertTrue(errors.contains(HEARING_RESUME_FUTURE));
    }

    @Test
    void invalidateHearingDatesInFutureTestNullCheck() {
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingStart("2222-11-01T12:11:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingFinish("2222-11-01T12:11:20.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingResume(null);
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingBreak(null);
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(2, errors.size());
        assertTrue(errors.contains(HEARING_START_FUTURE));
        assertTrue(errors.contains(HEARING_FINISH_FUTURE));
        assertFalse(errors.contains(HEARING_BREAK_FUTURE));
        assertFalse(errors.contains(HEARING_RESUME_FUTURE));
    }

    private void setValidHearingStartFinishTimes() {
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingStart("2019-11-01T12:11:00.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingTimingFinish("2019-11-01T12:11:20.000");
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
    }

    @Test
    void validateTwoJudges_DifferentJudges() {
        caseData.getHearingCollection().getFirst().getValue().setHearingSitAlone(TWO_JUDGES);
        caseData.getHearingCollection().getFirst().getValue().setJudge("Judge 1");
        caseData.getHearingCollection().getFirst().getValue().setAdditionalJudge("Judge 2");
        assertEquals(0, hearingMidEventValidation(caseData).size());
    }

    @Test
    void validateTwoJudges_SameJudges() {
        caseData.getHearingCollection().getFirst().getValue().setHearingSitAlone(TWO_JUDGES);
        caseData.getHearingCollection().getFirst().getValue().setJudge("Judge 1");
        caseData.getHearingCollection().getFirst().getValue().setAdditionalJudge("Judge 1");
        assertEquals(1, hearingMidEventValidation(caseData).size());
        assertEquals(TWO_JUDGES_ERROR.formatted("1"), hearingMidEventValidation(caseData).getFirst());
    }

    @Test
    void validateTwoJudges_NoJudgesProvided() {
        caseData.getHearingCollection().getFirst().getValue().setHearingSitAlone(TWO_JUDGES);
        caseData.getHearingCollection().getFirst().getValue().setJudge(null);
        caseData.getHearingCollection().getFirst().getValue().setAdditionalJudge(null);
        assertEquals(0, hearingMidEventValidation(caseData).size());
    }

    @Test
    void setHearingDaysAndDates_nullHearingCollection_doesNothing() {
        caseData.setHearingCollection(null);
        setHearingDaysAndDates(caseData);
        assertNull(caseData.getHearingCollection());
    }

    @Test
    void setHearingDaysAndDates_emptyHearingCollection_doesNothing() {
        caseData.setHearingCollection(new ArrayList<>());
        setHearingDaysAndDates(caseData);
        assertTrue(caseData.getHearingCollection().isEmpty());
    }

    @Test
    void setHearingDaysAndDates_nullDateCollection_skipsHearing() {
        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(null);
        HearingTypeItem item = new HearingTypeItem();
        item.setValue(hearingType);
        caseData.setHearingCollection(List.of(item));

        setHearingDaysAndDates(caseData);

        assertNull(hearingType.getHearingDates());
    }

    @Test
    void setHearingDaysAndDates_emptyDateCollection_skipsHearing() {
        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(new ArrayList<>());
        HearingTypeItem item = new HearingTypeItem();
        item.setValue(hearingType);
        caseData.setHearingCollection(List.of(item));

        setHearingDaysAndDates(caseData);

        assertNull(hearingType.getHearingDates());
    }

    @Test
    void setHearingDaysAndDates_singleListedDate_setsCountAndDate() {
        HearingType hearingType = buildHearingWithDates(
            hearingDate("2024-03-15T10:00:00.000", HEARING_STATUS_LISTED));
        caseData.setHearingCollection(List.of(hearingTypeItem(hearingType)));

        setHearingDaysAndDates(caseData);

        assertEquals("15 Mar 2024", hearingType.getHearingDates());
    }

    @Test
    void setHearingDaysAndDates_singleHeardDate_setsCountAndDate() {
        HearingType hearingType = buildHearingWithDates(
            hearingDate("2024-06-01T10:00:00.000", Constants.HEARING_STATUS_HEARD));
        caseData.setHearingCollection(List.of(hearingTypeItem(hearingType)));

        setHearingDaysAndDates(caseData);

        assertEquals("1 Jun 2024", hearingType.getHearingDates());
    }

    @Test
    void setHearingDaysAndDates_multipleDates_setsCountAndRange() {
        HearingType hearingType = buildHearingWithDates(
            hearingDate("2024-03-10T10:00:00.000", HEARING_STATUS_LISTED),
            hearingDate("2024-03-01T10:00:00.000", HEARING_STATUS_LISTED),
            hearingDate("2024-03-05T10:00:00.000", Constants.HEARING_STATUS_HEARD));
        caseData.setHearingCollection(List.of(hearingTypeItem(hearingType)));

        setHearingDaysAndDates(caseData);

        assertEquals("1 Mar 2024 - 10 Mar 2024", hearingType.getHearingDates());
    }

    @Test
    void setHearingDaysAndDates_noValidStatusDates_setsDashAndTbc() {
        HearingType hearingType = buildHearingWithDates(
            hearingDate("2024-03-01T10:00:00.000", Constants.HEARING_STATUS_POSTPONED));
        caseData.setHearingCollection(List.of(hearingTypeItem(hearingType)));

        setHearingDaysAndDates(caseData);

        assertEquals("-", hearingType.getHearingDates());
    }

    @Test
    void setHearingDaysAndDates_mixedStatuses_onlyCountsListedAndHeard() {
        HearingType hearingType = buildHearingWithDates(
            hearingDate("2024-03-01T10:00:00.000", HEARING_STATUS_LISTED),
            hearingDate("2024-03-05T10:00:00.000", Constants.HEARING_STATUS_POSTPONED),
            hearingDate("2024-03-10T10:00:00.000", Constants.HEARING_STATUS_HEARD));
        caseData.setHearingCollection(List.of(hearingTypeItem(hearingType)));

        setHearingDaysAndDates(caseData);
        assertEquals("1 Mar 2024 - 10 Mar 2024", hearingType.getHearingDates());

    }

    @Test
    void setHearingDaysAndDates_multipleHearings_processesBothIndependently() {
        HearingType hearing1 = buildHearingWithDates(
            hearingDate("2024-03-01T10:00:00.000", HEARING_STATUS_LISTED),
            hearingDate("2024-03-02T10:00:00.000", HEARING_STATUS_LISTED));
        HearingType hearing2 = buildHearingWithDates(
            hearingDate("2024-06-10T10:00:00.000", Constants.HEARING_STATUS_POSTPONED));
        caseData.setHearingCollection(List.of(hearingTypeItem(hearing1), hearingTypeItem(hearing2)));

        setHearingDaysAndDates(caseData);

        assertEquals("1 Mar 2024 - 2 Mar 2024", hearing1.getHearingDates());
        assertEquals("-", hearing2.getHearingDates());
    }

    private static HearingType buildHearingWithDates(DateListedTypeItem... dates) {
        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(new ArrayList<>(List.of(dates)));
        return hearingType;
    }

    private static HearingTypeItem hearingTypeItem(HearingType hearingType) {
        HearingTypeItem item = new HearingTypeItem();
        item.setValue(hearingType);
        return item;
    }

    private static DateListedTypeItem hearingDate(String listedDate, String status) {
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(status);
        DateListedTypeItem item = new DateListedTypeItem();
        item.setValue(dateListedType);
        return item;
    }
}
