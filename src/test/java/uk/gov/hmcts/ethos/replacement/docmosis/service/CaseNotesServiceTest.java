package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.GenericTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CaseNote;
import uk.gov.hmcts.ecm.compat.common.idam.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CaseNotesServiceTest {
    private static final String EMAIL = "email@email.com";
    private static final String NAME = "Mr Magoo";

    @MockitoBean
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

    @Test
    void populateCaseNoteList_noCaseNotes_returnsError() {
        CaseData caseData = new CaseData();
        List<String> errors = caseNotesService.populateCaseNoteList(caseData);
        assertEquals(1, errors.size());
        assertEquals("There are no telephone notes to edit or delete", errors.getFirst());
    }

    @Test
    void populateCaseNoteList_withCaseNotes_populatesList() {
        CaseNote note = CaseNote.builder().title("My Note").note("Content").build();
        note.setDate("01 Jan 2024 10:00");
        note.setAuthor("Test Author");
        GenericTypeItem<CaseNote> item = new GenericTypeItem<>();
        item.setId("uuid-1");
        item.setValue(note);

        CaseData caseData = new CaseData();
        caseData.setCaseNotesCollection(new ArrayList<>(List.of(item)));

        List<String> errors = caseNotesService.populateCaseNoteList(caseData);

        assertEquals(0, errors.size());
        assertNotNull(caseData.getCaseNoteDynamicList());
        assertEquals(1, caseData.getCaseNoteDynamicList().getListItems().size());
        assertEquals("uuid-1", caseData.getCaseNoteDynamicList().getListItems().getFirst().getCode());
        assertEquals("My Note - 01 Jan 2024 10:00 - Test Author",
            caseData.getCaseNoteDynamicList().getListItems().getFirst().getLabel());
        assertNull(caseData.getAddCaseNote());
        assertNull(caseData.getEditOrDeleteCaseNote());
    }

    @Test
    void populateEditCaseNote_editSetsAddCaseNote() {
        CaseNote note = CaseNote.builder().title("Existing Note").note("Existing Content").build();
        GenericTypeItem<CaseNote> item = new GenericTypeItem<>();
        item.setId("uuid-1");
        item.setValue(note);

        DynamicValueType selectedValue = new DynamicValueType();
        selectedValue.setCode("uuid-1");
        DynamicFixedListType dynamicList = new DynamicFixedListType();
        dynamicList.setValue(selectedValue);

        CaseData caseData = new CaseData();
        caseData.setCaseNotesCollection(new ArrayList<>(List.of(item)));
        caseData.setCaseNoteDynamicList(dynamicList);
        caseData.setEditOrDeleteCaseNote("Edit");

        caseNotesService.populateEditCaseNote(caseData);

        assertEquals(note, caseData.getAddCaseNote());
    }

    @Test
    void populateEditCaseNote_deleteDoesNothing() {
        CaseNote note = CaseNote.builder().title("Note").note("Content").build();
        GenericTypeItem<CaseNote> item = new GenericTypeItem<>();
        item.setId("uuid-1");
        item.setValue(note);

        DynamicValueType selectedValue = new DynamicValueType();
        selectedValue.setCode("uuid-1");
        DynamicFixedListType dynamicList = new DynamicFixedListType();
        dynamicList.setValue(selectedValue);

        CaseData caseData = new CaseData();
        caseData.setCaseNotesCollection(new ArrayList<>(List.of(item)));
        caseData.setCaseNoteDynamicList(dynamicList);
        caseData.setEditOrDeleteCaseNote("Delete");

        caseNotesService.populateEditCaseNote(caseData);

        assertNull(caseData.getAddCaseNote());
        assertEquals(1, caseData.getCaseNotesCollection().size());
    }

    @Test
    void populateEditCaseNote_editNullList_throwsException() {
        CaseData caseData = new CaseData();
        caseData.setEditOrDeleteCaseNote("Edit");
        assertThrows(IllegalArgumentException.class,
            () -> caseNotesService.populateEditCaseNote(caseData));
    }

    @Test
    void populateEditCaseNote_invalidOption_throwsException() {
        CaseData caseData = new CaseData();
        caseData.setEditOrDeleteCaseNote("Invalid");
        assertThrows(IllegalArgumentException.class,
            () -> caseNotesService.populateEditCaseNote(caseData));
    }

    @Test
    void submitCaseNoteUpdate_editUpdatesNote() {
        CaseNote note = CaseNote.builder().title("Old Title").note("Old Content").build();
        GenericTypeItem<CaseNote> item = new GenericTypeItem<>();
        item.setId("uuid-1");
        item.setValue(note);

        DynamicValueType selectedValue = new DynamicValueType();
        selectedValue.setCode("uuid-1");
        DynamicFixedListType dynamicList = new DynamicFixedListType();
        dynamicList.setValue(selectedValue);

        CaseNote updatedNote = CaseNote.builder().title("New Title").note("New Content").build();

        CaseData caseData = new CaseData();
        caseData.setCaseNotesCollection(new ArrayList<>(List.of(item)));
        caseData.setCaseNoteDynamicList(dynamicList);
        caseData.setEditOrDeleteCaseNote("Edit");
        caseData.setAddCaseNote(updatedNote);

        caseNotesService.submitCaseNoteUpdate(caseData);

        assertEquals(1, caseData.getCaseNotesCollection().size());
        assertEquals("New Title", caseData.getCaseNotesCollection().getFirst().getValue().getTitle());
        assertEquals("New Content", caseData.getCaseNotesCollection().getFirst().getValue().getNote());
        assertNull(caseData.getCaseNoteDynamicList());
        assertNull(caseData.getAddCaseNote());
        assertNull(caseData.getEditOrDeleteCaseNote());
    }

    @Test
    void submitCaseNoteUpdate_deleteRemovesNote() {
        CaseNote note1 = CaseNote.builder().title("Note 1").note("Content 1").build();
        GenericTypeItem<CaseNote> item1 = new GenericTypeItem<>();
        item1.setId("uuid-1");
        item1.setValue(note1);

        CaseNote note2 = CaseNote.builder().title("Note 2").note("Content 2").build();
        GenericTypeItem<CaseNote> item2 = new GenericTypeItem<>();
        item2.setId("uuid-2");
        item2.setValue(note2);

        DynamicValueType selectedValue = new DynamicValueType();
        selectedValue.setCode("uuid-1");
        DynamicFixedListType dynamicList = new DynamicFixedListType();
        dynamicList.setValue(selectedValue);

        CaseData caseData = new CaseData();
        caseData.setCaseNotesCollection(new ArrayList<>(List.of(item1, item2)));
        caseData.setCaseNoteDynamicList(dynamicList);
        caseData.setEditOrDeleteCaseNote("Delete");

        caseNotesService.submitCaseNoteUpdate(caseData);

        assertEquals(1, caseData.getCaseNotesCollection().size());
        assertEquals("uuid-2", caseData.getCaseNotesCollection().getFirst().getId());
        assertNull(caseData.getCaseNoteDynamicList());
        assertNull(caseData.getAddCaseNote());
        assertNull(caseData.getEditOrDeleteCaseNote());
    }

    @Test
    void submitCaseNoteUpdate_invalidOption_throwsException() {
        CaseData caseData = new CaseData();
        caseData.setEditOrDeleteCaseNote("Invalid");
        assertThrows(IllegalArgumentException.class,
            () -> caseNotesService.submitCaseNoteUpdate(caseData));
    }
}
