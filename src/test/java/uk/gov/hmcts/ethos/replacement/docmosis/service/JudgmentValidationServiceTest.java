package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements.NO_HEARINGS;

class JudgmentValidationServiceTest {

    private JudgmentValidationService judgmentValidationService;

    private CaseDetails caseDetails1;

    @BeforeEach
    void setup() throws Exception {
        judgmentValidationService = new JudgmentValidationService();
        caseDetails1 = generateCaseDetails();
    }

    private CaseDetails generateCaseDetails() throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("caseDetailsTest1.json")).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    void populateJudgmentDateOfHearingTest() throws ParseException {
        DynamicJudgements.dynamicJudgements(caseDetails1.getCaseData());
        caseDetails1.getCaseData().getJudgementCollection().getFirst().getValue()
            .getDynamicJudgementHearing().setValue(
                caseDetails1.getCaseData().getJudgementCollection().getFirst().getValue().getDynamicJudgementHearing()
                    .getListItems().getFirst());
        judgmentValidationService.validateJudgments(caseDetails1.getCaseData());
        assertEquals("2019-11-01", caseDetails1.getCaseData().getJudgementCollection().getFirst()
            .getValue().getJudgmentHearingDate());
    }

    @Test
    void populateJudgmentZeroHearings() throws ParseException {
        var caseData = caseDetails1.getCaseData();
        caseData.setHearingCollection(null);
        DynamicJudgements.dynamicJudgements(caseData);
        var dynamicValue = DynamicListHelper.getDynamicValue(NO_HEARINGS);
        assertEquals(dynamicValue, caseData.getJudgementCollection().getFirst().getValue().getDynamicJudgementHearing()
            .getListItems().getFirst());

        judgmentValidationService.validateJudgments(caseDetails1.getCaseData());
        assertNull(caseData.getJudgementCollection().getFirst().getValue().getDynamicJudgementHearing());
        assertNull(caseData.getJudgementCollection().getFirst().getValue().getJudgmentHearingDate());
    }
}
