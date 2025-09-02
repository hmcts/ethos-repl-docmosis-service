package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_SETTLED;
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
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_DAY_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_NUMBER_ERROR;

public class HearingsHelperTest {

    private CaseData caseData;

    @Before
    public void setUp() throws Exception {
        caseData = generateCaseDetails("caseDetailsTestHearingDetailsUpdate.json").getCaseData();
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }


    @Test
    public void hearingMidEventValidationNumberError() {

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
    public void hearingMidEventValidationDayError() {

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
    public void updatePostponedDate() {

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
    public void findDateOfHearingTest() {
        var hearingDate = caseData.getHearingCollection().getFirst().getValue()
                .getHearingDateCollection().getFirst().getValue().getListedDate().substring(0, 10);
        var hearingNumber = findHearingNumber(caseData, hearingDate);
        assertEquals(hearingNumber, caseData.getHearingCollection().getFirst().getValue().getHearingNumber());
    }

    @Test
    public void findDateOfHearing_DateNotInHearing() {
        assertNull(findHearingNumber(caseData, "1970-10-01"));
    }

    @Test
    public void validateStartFinishTime_validTime () {
        setValidHearingStartFinishTimes();
        caseData.getHearingCollection().getFirst().getValue()
                .getHearingDateCollection().getFirst().getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    public void validateBreakResumeTime_invalidBreak () {
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
    public void validateBreakResumeTime_invalidResume () {
        setValidHearingStartFinishTimes();
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
                .getHearingDateCollection().getFirst().getValue().setHearingTimingResume("2019-11-01T00:00:00.000");

        List<String> errors = hearingTimeValidation(caseData);
        var hearingNumber = caseData.getHearingsCollectionForUpdate().getFirst().getValue().getHearingNumber();
        assertEquals(1, errors.size());
        assertEquals(String.format(HEARING_BREAK_RESUME_INVALID, hearingNumber), errors.getFirst());
    }

    @Test
    public void validateBreakResumeTime_nullBreakResume () {
        setValidHearingStartFinishTimes();
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
                .getHearingDateCollection().getFirst().getValue().setHearingTimingBreak(null);
        caseData.getHearingsCollectionForUpdate().getFirst().getValue()
                .getHearingDateCollection().getFirst().getValue().setHearingTimingResume(null);
        List<String> errors = hearingTimeValidation(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    public void validateStartFinishTime_sameTime () {
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
    public void validateStartFinishTime_finishTimeBeforeStart () {
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
    public void validateHearingDatesInPastTest() {
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
    public void validateIsDateInFutureConsideringDST() {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(25);
        LocalDateTime now = LocalDateTime.now(UTC);
        boolean val = isDateInFuture(dateTime.toString(), now);
        assertFalse(val);
    }

    @Test
    public void invalidateHearingDatesInFutureTest() {
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
    public void invalidateHearingDatesInFutureTestNullCheck() {
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
    public void validateTwoJudges_DifferentJudges() {
        caseData.getHearingCollection().getFirst().getValue().setHearingSitAlone(TWO_JUDGES);
        caseData.getHearingCollection().getFirst().getValue().setJudge("Judge 1");
        caseData.getHearingCollection().getFirst().getValue().setAdditionalJudge("Judge 2");
        assertEquals(0, hearingMidEventValidation(caseData).size());
    }

    @Test
    public void validateTwoJudges_SameJudges() {
        caseData.getHearingCollection().getFirst().getValue().setHearingSitAlone(TWO_JUDGES);
        caseData.getHearingCollection().getFirst().getValue().setJudge("Judge 1");
        caseData.getHearingCollection().getFirst().getValue().setAdditionalJudge("Judge 1");
        assertEquals(1, hearingMidEventValidation(caseData).size());
        assertEquals(TWO_JUDGES_ERROR.formatted("1"), hearingMidEventValidation(caseData).getFirst());
    }

    @Test
    public void validateTwoJudges_NoJudgesProvided() {
        caseData.getHearingCollection().getFirst().getValue().setHearingSitAlone(TWO_JUDGES);
        caseData.getHearingCollection().getFirst().getValue().setJudge(null);
        caseData.getHearingCollection().getFirst().getValue().setAdditionalJudge(null);
        assertEquals(0, hearingMidEventValidation(caseData).size());
    }
}
