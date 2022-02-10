package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.LettersHelper.DOUBLE_SPACE_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.LettersHelper.NEW_LINE_ERROR;

public class LettersHelperTest {

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
        casedata.getRespondentCollection().get(0).getValue().setRespondentName("Double  Space and New\nLine");

        var representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfRepresentative("No Errors In Name");
        var representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setValue(representedTypeR);
        casedata.setRepCollection(List.of(representedTypeRItem));

        List<String> errors = LettersHelper.checkNamesForInvalidCharacters(casedata);

        assertEquals(4, errors.size());
        assertEquals("Double  Space" + DOUBLE_SPACE_ERROR, errors.get(0));
        assertEquals("New\nLine" + NEW_LINE_ERROR, errors.get(1));
        assertEquals("Double  Space and New\nLine" + DOUBLE_SPACE_ERROR, errors.get(2));
        assertEquals("Double  Space and New\nLine" + NEW_LINE_ERROR, errors.get(3));

    }
}
