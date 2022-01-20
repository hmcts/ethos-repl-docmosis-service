package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Objects;

import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements.NO_HEARINGS;

class JudgmentValidationServiceTest {

    private JudgmentValidationService judgmentValidationService;

    private CaseDetails caseDetails1;

    @BeforeEach
    public void setup() throws Exception {
        judgmentValidationService = new JudgmentValidationService();
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    void populateJudgmentDateOfHearingTest() throws ParseException {
        DynamicJudgements.dynamicJudgements(caseDetails1.getCaseData());
        caseDetails1.getCaseData().getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().setValue(caseDetails1.getCaseData().getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().getListItems().get(0));
        judgmentValidationService.validateJudgments(caseDetails1.getCaseData());
        Assertions.assertEquals("2019-11-01", caseDetails1.getCaseData().getJudgementCollection().get(0).getValue().getJudgmentHearingDate());
    }

    @Test
    void populateJudgmentZeroHearings() throws ParseException {
        var caseData = caseDetails1.getCaseData();
        caseData.setHearingCollection(null);
        DynamicJudgements.dynamicJudgements(caseData);
        var dynamicValue = DynamicListHelper.getDynamicValue(NO_HEARINGS);
        Assertions.assertEquals(dynamicValue, caseData.getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().getListItems().get(0));

        judgmentValidationService.validateJudgments(caseDetails1.getCaseData());
        Assertions.assertNull(caseData.getJudgementCollection().get(0).getValue().getDynamicJudgementHearing());
        Assertions.assertNull(caseData.getJudgementCollection().get(0).getValue().getJudgmentHearingDate());
    }
}
