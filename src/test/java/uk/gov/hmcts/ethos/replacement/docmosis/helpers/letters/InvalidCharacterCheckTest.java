package uk.gov.hmcts.ethos.replacement.docmosis.helpers.letters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.letters.InvalidCharacterCheck.DOUBLE_SPACE_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.letters.InvalidCharacterCheck.NEW_LINE_ERROR;

public class InvalidCharacterCheckTest {

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
    public void checkInvalidCharactersInNames() {
        var casedata = caseDetails1.getCaseData();
        casedata.setClaimant("Double  Space");
        casedata.getRepresentativeClaimantType().setNameOfRepresentative("New\nLine");
        casedata.getRespondentCollection().getFirst().getValue().setRespondentName("Double  Space and New\nLine");

        var representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfRepresentative("No Errors In Name");
        var representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setValue(representedTypeR);
        casedata.setRepCollection(List.of(representedTypeRItem));

        List<String> errors = InvalidCharacterCheck.checkNamesForInvalidCharacters(casedata, "letter");
        assertEquals(4, errors.size());
        assertEquals(String.format(DOUBLE_SPACE_ERROR, "Claimant Double  Space",
                casedata.getEthosCaseReference(), "letter"), errors.getFirst());
        assertEquals(String.format(NEW_LINE_ERROR, "Claimant Rep New\nLine",
                casedata.getEthosCaseReference(), "letter"), errors.get(1));
        assertEquals(String.format(DOUBLE_SPACE_ERROR, "Respondent Double  Space and New\nLine",
                casedata.getEthosCaseReference(), "letter"), errors.get(2));
        assertEquals(String.format(NEW_LINE_ERROR, "Respondent Double  Space and New\nLine",
                casedata.getEthosCaseReference(), "letter"), errors.get(3));
    }

    @Test
    public void checkInvalidCharactersInNamesNoClaimantResp() {
        var casedata = caseDetails1.getCaseData();
        casedata.setClaimant("Single Space");
        casedata.setClaimantRepresentedQuestion("No");
        casedata.getRepresentativeClaimantType().setNameOfRepresentative("New\nLine");
        casedata.setRespondentCollection(null);
        casedata.setRepCollection(null);
        List<String> errors = InvalidCharacterCheck.checkNamesForInvalidCharacters(casedata, "letter");
        assertEquals(0, errors.size());
    }
}