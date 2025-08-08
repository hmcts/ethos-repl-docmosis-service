package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.GenericTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CaseNote;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CaseNotesServiceTest {
    private static final String EMAIL = "email@email.com";
    private static final String NAME = "Mr Magoo";

    @MockBean
    private UserService userService;
    private UserDetails userDetails;
    private CaseNotesService caseNotesService;
    private CaseNote caseNote;
    private String userToken;

    @BeforeEach
    void setUp() {
        caseNotesService = new CaseNotesService(userService);
        caseNote = CaseNote.builder()
            .title("Test note")
            .note("Will multiples ever get released?")
            .build();
        userToken = "authString";
        userDetails = new UserDetails();
        userDetails.setEmail(EMAIL);
        userDetails.setName(NAME);
        when(userService.getUserDetails(userToken)).thenReturn(userDetails);
    }

    @Test
    void verifySingleCaseIsSetWithCaseNotes() {
        CaseData caseData = new CaseData();
        caseData.setAddCaseNote(caseNote);
        caseNotesService.addCaseNote(caseData, userToken);
        List<GenericTypeItem<CaseNote>> caseNoteCollection = caseData.getCaseNotesCollection();
        CaseNote createdNote = caseNoteCollection.getFirst().getValue();
        assertEquals(1, caseNoteCollection.size());
        assertEquals(createdNote, caseNote);
        assertEquals(NAME, createdNote.getAuthor());
        assertNotNull(createdNote.getDate());
        assertNull(caseData.getAddCaseNote());
    }

    @Test
    void verifyCaseNoteWithNoIdamName() {
        userDetails.setName(null);
        CaseData caseData = new CaseData();
        caseData.setAddCaseNote(caseNote);
        caseNotesService.addCaseNote(caseData, userToken);
        List<GenericTypeItem<CaseNote>> caseNoteCollection = caseData.getCaseNotesCollection();
        CaseNote createdNote = caseNoteCollection.getFirst().getValue();
        assertEquals(1, caseNoteCollection.size());
        assertEquals(createdNote, caseNote);
        assertEquals(EMAIL, createdNote.getAuthor());
        assertNotNull(createdNote.getDate());
        assertNull(caseData.getAddCaseNote());
    }

    @Test
    void verifySingleCaseIsSetWithCaseNotes_nullUserDetails() {
        when(userService.getUserDetails(userToken)).thenReturn(null);
        CaseData caseData = new CaseData();
        caseData.setAddCaseNote(caseNote);
        assertDoesNotThrow(() -> caseNotesService.addCaseNote(caseData, userToken));

        List<GenericTypeItem<CaseNote>> caseNoteCollection = caseData.getCaseNotesCollection();
        assertEquals(1, caseNoteCollection.size());
        CaseNote createdNote = caseNoteCollection.getFirst().getValue();
        assertEquals(createdNote, caseNote);
        assertEquals("Unknown", createdNote.getAuthor());
        assertNotNull(createdNote.getDate());
        assertNull(caseData.getAddCaseNote());
    }
}