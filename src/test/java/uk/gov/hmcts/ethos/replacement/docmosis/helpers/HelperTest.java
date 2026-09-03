package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AdditionalCaseInfoType;
import uk.gov.hmcts.ecm.compat.common.idam.models.UserDetails;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.YES;

public class HelperTest {
    private static final LocalDate ERA_START_DATE = LocalDate.of(2026, 10, 1);

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
    public void nullCheck() {
        assertEquals("Value ' example ' ", Helper.nullCheck("Value \" example \" "));
        assertEquals("", Helper.nullCheck(null));
        assertEquals("Value example", Helper.nullCheck("Value example"));
        assertEquals("Value ' example '", Helper.nullCheck("Value ' example '"));
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
    public void setEraFlagByReceiptDateSetsNoAndInitialisesAdditionalCaseInfoBeforeCutoff() {
        CaseData caseData = new CaseData();
        caseData.setReceiptDate("2026-09-30");

        Helper.setEraFlagByReceiptDate(caseData, ERA_START_DATE);

        assertEquals(NO, caseData.getAdditionalCaseInfoType().getEra());
    }

    @Test
    public void setEraFlagByReceiptDateDoesNotChangeEraOnOrAfterCutoff() {
        AdditionalCaseInfoType additionalCaseInfoType = new AdditionalCaseInfoType();
        additionalCaseInfoType.setEra(YES);
        CaseData caseData = new CaseData();
        caseData.setAdditionalCaseInfoType(additionalCaseInfoType);

        caseData.setReceiptDate("2026-10-01");
        Helper.setEraFlagByReceiptDate(caseData, ERA_START_DATE);
        assertEquals(YES, caseData.getAdditionalCaseInfoType().getEra());

        caseData.setReceiptDate("2026-10-02");
        Helper.setEraFlagByReceiptDate(caseData, ERA_START_DATE);
        assertEquals(YES, caseData.getAdditionalCaseInfoType().getEra());
    }

    @Test
    public void setEraFlagByReceiptDateIgnoresNullAndBlankInputs() {
        Helper.setEraFlagByReceiptDate(null, ERA_START_DATE);

        CaseData caseData = new CaseData();
        Helper.setEraFlagByReceiptDate(caseData, ERA_START_DATE);
        assertNull(caseData.getAdditionalCaseInfoType());

        caseData.setReceiptDate("");
        Helper.setEraFlagByReceiptDate(caseData, ERA_START_DATE);
        assertNull(caseData.getAdditionalCaseInfoType());
    }

    @Test
    public void setEraFlagByReceiptDateIgnoresMalformedReceiptDates() {
        AdditionalCaseInfoType additionalCaseInfoType = new AdditionalCaseInfoType();
        additionalCaseInfoType.setEra(YES);
        CaseData caseData = new CaseData();
        caseData.setAdditionalCaseInfoType(additionalCaseInfoType);
        caseData.setReceiptDate("01-10-2026");

        Helper.setEraFlagByReceiptDate(caseData, ERA_START_DATE);

        assertEquals(YES, caseData.getAdditionalCaseInfoType().getEra());
    }

    @Test
    public void setEraFlagByReceiptDateUsesConfiguredStartDate() {
        CaseData caseData = new CaseData();
        caseData.setReceiptDate("2026-09-30");

        Helper.setEraFlagByReceiptDate(caseData, LocalDate.of(2026, 9, 1));

        assertNull(caseData.getAdditionalCaseInfoType());
    }

    @Test
    public void setEraFlagByReceiptDateAcceptsIsoTimestampStartDate() {
        CaseData caseData = new CaseData();
        caseData.setReceiptDate("2026-08-31");

        Helper.setEraFlagByReceiptDate(caseData, "2026-09-01T00:00:00Z");

        assertEquals(NO, caseData.getAdditionalCaseInfoType().getEra());
    }

}
