package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
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
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.findHearingNumber;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_DAY_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_NUMBER_ERROR;

public class HearingsHelperTest {

    private CaseDetails caseDetails1;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTestHearingDetailsUpdate.json");
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }


    @Test
    public void hearingMidEventValidationNumberError() {

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue().setHearingNumber(null);

        assertEquals(1, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_NUMBER_ERROR,
                HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue().setHearingNumber("");

        assertEquals(1, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_NUMBER_ERROR,
                HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

    }

    @Test
    public void hearingMidEventValidationDayError() {

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setListedDate(null);

        assertEquals(1, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_DAY_ERROR, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setListedDate("");

        assertEquals(1, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_DAY_ERROR, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .setHearingDateCollection(null);

        assertEquals(0, HearingsHelper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

    }

    @Test
    public void updatePostponedDate() {

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_POSTPONED);

        HearingsHelper.updatePostponedDate(caseDetails1.getCaseData());

        assertNotNull(caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getPostponedDate());

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_SETTLED);

        HearingsHelper.updatePostponedDate(caseDetails1.getCaseData());

        assertNull(caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getPostponedDate());
    }

    @Test
    public void findDateOfHearingTest() {
        var hearingDate = caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getListedDate().substring(0, 10);
        var hearingNumber = findHearingNumber(caseDetails1.getCaseData(), hearingDate);
        assertEquals(hearingNumber, caseDetails1.getCaseData().getHearingCollection().get(0).getValue().getHearingNumber());
    }

    @Test
    public void findDateOfHearing_DateNotInHearing() {
        assertNull(findHearingNumber(caseDetails1.getCaseData(), "1970-10-01"));
    }

    @Test
    public void validateStartFinishTime_validTime () {
        setValidHearingStartFinishTimes();
        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(0, errors.size());
    }

    @Test
    public void validateBreakResumeTime_invalidBreak () {
        setValidHearingStartFinishTimes();
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingBreak("2019-11-01T00:00:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);

        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        var hearingNumber = caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingNumber();
        assertEquals(1, errors.size());
        assertEquals(String.format(HEARING_BREAK_RESUME_INVALID, hearingNumber), errors.get(0));
    }

    @Test
    public void validateBreakResumeTime_invalidResume () {
        setValidHearingStartFinishTimes();
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingResume("2019-11-01T00:00:00.000");

        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        var hearingNumber = caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingNumber();
        assertEquals(1, errors.size());
        assertEquals(String.format(HEARING_BREAK_RESUME_INVALID, hearingNumber), errors.get(0));
    }

    @Test
    public void validateBreakResumeTime_nullBreakResume () {
        setValidHearingStartFinishTimes();
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingBreak(null);
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingResume(null);
        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(0, errors.size());
    }

    @Test
    public void validateStartFinishTime_sameTime () {
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingStart("2019-11-01T12:11:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingFinish("2019-11-01T12:11:00.000"); // Same time as start time
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
        var hearingNumber = caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingNumber();

        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(1, errors.size());
        assertEquals(HEARING_FINISH_INVALID + hearingNumber, errors.get(0));
    }

    @Test
    public void validateStartFinishTime_finishTimeBeforeStart () {
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingStart("2019-11-01T12:11:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingFinish("2019-11-01T12:10:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
        var hearingNumber = caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingNumber();

        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(1, errors.size());
        assertEquals(HEARING_FINISH_INVALID + hearingNumber, errors.get(0));
    }

    @Test
    public void validateHearingDatesInPastTest() {
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setHearingTimingBreak("2021-12-19T10:00:00");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setHearingTimingResume("2021-12-19T10:00:00");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setHearingTimingFinish("2021-12-19T10:10:00");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setHearingTimingStart("2021-12-19T10:00:00");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(0, errors.size());
    }

    @Test
    public void validateIsDateInFutureConsideringDST() {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(25);
        LocalDateTime now = LocalDateTime.now(UTC);
        boolean val = HearingsHelper.isDateInFuture(dateTime.toString(), now);
        assertFalse(val);
    }

    @Test
    public void invalidateHearingDatesInFutureTest() {
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingStart("2222-11-01T12:11:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingFinish("2222-11-01T12:11:20.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingResume("2222-11-01T12:11:20.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingBreak("2222-11-01T12:11:20.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(4, errors.size());
        assertTrue(errors.contains(HEARING_START_FUTURE));
        assertTrue(errors.contains(HEARING_FINISH_FUTURE));
        assertTrue(errors.contains(HEARING_BREAK_FUTURE));
        assertTrue(errors.contains(HEARING_RESUME_FUTURE));
    }

    @Test
    public void invalidateHearingDatesInFutureTestNullCheck() {
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingStart("2222-11-01T12:11:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingFinish("2222-11-01T12:11:20.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingResume(null);
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingBreak(null);
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
        List<String> errors = HearingsHelper.hearingTimeValidation(caseDetails1.getCaseData());
        assertEquals(2, errors.size());
        assertTrue(errors.contains(HEARING_START_FUTURE));
        assertTrue(errors.contains(HEARING_FINISH_FUTURE));
        assertFalse(errors.contains(HEARING_BREAK_FUTURE));
        assertFalse(errors.contains(HEARING_RESUME_FUTURE));
    }

    private void setValidHearingStartFinishTimes() {
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingStart("2019-11-01T12:11:00.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingTimingFinish("2019-11-01T12:11:20.000");
        caseDetails1.getCaseData().getHearingsCollectionForUpdate().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_HEARD);
    }
}
