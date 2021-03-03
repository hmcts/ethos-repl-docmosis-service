package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_SETTLED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_DAY_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.HEARING_CREATION_NUMBER_ERROR;

public class HelperTest {

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetailsScot1;
    private CaseDetails caseDetailsScot2;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetailsScot1 = generateCaseDetails("caseDetailsScotTest1.json");
        caseDetailsScot2 = generateCaseDetails("caseDetailsScotTest2.json");
    }

    public static UserDetails getUserDetails() {
        UserDetails userDetails = new UserDetails();
        userDetails.setUid("id");
        userDetails.setEmail("mail@mail.com");
        userDetails.setFirstName("Mike");
        userDetails.setLastName("Jordan");
        userDetails.setRoles(Collections.singletonList("role"));
        return userDetails;
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    public void getDocumentName() {
        String expected = "EM-TRB-EGW-ENG-00029_4.2";
        assertEquals(expected, Helper.getDocumentName(caseDetails4.getCaseData().getCorrespondenceType(),
                caseDetails4.getCaseData().getCorrespondenceScotType()));
    }

    @Test
    public void getActiveRespondentsAllFound() {
        int activeRespondentsFound = 3;

        List<RespondentSumTypeItem> activeRespondents = Helper.getActiveRespondents(caseDetails1.getCaseData());

        assertEquals(activeRespondentsFound, activeRespondents.size());
    }

    @Test
    public void getActiveRespondentsSomeFound() {
        int activeRespondentsFound = 2;

        List<RespondentSumTypeItem> activeRespondents = Helper.getActiveRespondents(caseDetailsScot1.getCaseData());

        assertEquals(activeRespondentsFound, activeRespondents.size());
    }

    @Test
    public void getActiveRespondentsNoneFound() {
        int activeRespondentsFound = 0;

        List<RespondentSumTypeItem> activeRespondents = Helper.getActiveRespondents(caseDetailsScot2.getCaseData());

        assertEquals(activeRespondentsFound, activeRespondents.size());
    }

    @Test
    public void hearingMidEventValidationNumberError() {

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue().setHearingNumber(null);

        assertEquals(1, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_NUMBER_ERROR, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue().setHearingNumber("");

        assertEquals(1, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_NUMBER_ERROR, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

    }

    @Test
    public void hearingMidEventValidationDayError() {

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setListedDate(null);

        assertEquals(1, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_DAY_ERROR, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setListedDate("");

        assertEquals(1, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

        assertEquals(HEARING_CREATION_DAY_ERROR, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).get(0));

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .setHearingDateCollection(null);

        assertEquals(0, Helper.hearingMidEventValidation(caseDetails1.getCaseData()).size());

    }

    @Test
    public void updatePostponedDate() {

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_POSTPONED);

        Helper.updatePostponedDate(caseDetails1.getCaseData());

        assertNotNull(caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getPostponedDate());

        caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setHearingStatus(HEARING_STATUS_SETTLED);

        Helper.updatePostponedDate(caseDetails1.getCaseData());

        assertNull(caseDetails1.getCaseData().getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getPostponedDate());
    }

}
