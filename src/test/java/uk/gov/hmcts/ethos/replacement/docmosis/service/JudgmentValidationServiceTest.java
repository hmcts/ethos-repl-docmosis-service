package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class JudgmentValidationServiceTest {

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
    public void populateJudgmentDateOfHearingTest() throws ParseException {
        DynamicJudgements.dynamicJudgements(caseDetails1.getCaseData());
        caseDetails1.getCaseData().getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().setValue(caseDetails1.getCaseData().getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().getListItems().get(0));
        judgmentValidationService.validateJudgments(caseDetails1.getCaseData());
        assertEquals("2019-11-01", caseDetails1.getCaseData().getJudgementCollection().get(0).getValue().getJudgmentHearingDate());

    }
}
