package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_SETTLED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.findHearingNumber;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_DAY_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_NUMBER_ERROR;

public class HearingsHelperTest {

    private CaseDetails caseDetails1;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
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
}
