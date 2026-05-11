package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.GenericTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CaseNote;
import uk.gov.hmcts.ecm.compat.common.idam.models.UserDetails;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Service for managing case notes.
 */
@Slf4j
@RequiredArgsConstructor
@Service("caseNotesService")
public class CaseNotesService {
    private final UserService userService;

    /**
     * Adds a case note to the case data.
     * @param caseData the case data to which the case note will be added
     * @param userToken the user token for authentication and user details retrieval
     */
    public void addCaseNote(CaseData caseData, String userToken) {
        CaseNote caseNote = caseData.getAddCaseNote();

        GenericTypeItem<CaseNote> caseNoteListTypeItem = getCaseNoteGenericTypeItem(userToken, caseNote);

        if (caseData.getCaseNotesCollection() == null) {
            caseData.setCaseNotesCollection(new ArrayList<>());
        }
        caseData.getCaseNotesCollection().add(caseNoteListTypeItem);
        caseData.setAddCaseNote(null);
    }

    /**
     * Populates the case note dynamic list for the given case data. If there are no case notes, an error message
     * is returned indicating that there are no telephone notes to edit or delete.
     * @param caseData the case data
     * @return a list of error messages if there are no case notes, an empty list otherwise
     */
    public List<String> populateCaseNoteList(CaseData caseData) {
        if (isEmpty(caseData.getCaseNotesCollection())) {
            return List.of("There are no telephone notes to edit or delete");
        }
        clearOldValues(caseData);
        List<DynamicValueType> caseNoteItems = caseData.getCaseNotesCollection().stream()
            .filter(ObjectUtils::isNotEmpty)
            .map(caseNote -> {
                var dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(caseNote.getId());
                dynamicValueType.setLabel(getCaseNoteLabel(caseNote));
                return dynamicValueType;
            })
            .toList();
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(caseNoteItems);
        caseData.setCaseNoteDynamicList(dynamicFixedListType);
        return new ArrayList<>();
    }

    private static String getCaseNoteLabel(GenericTypeItem<CaseNote> caseNote) {
        return caseNote.getValue().getTitle() + " - " + caseNote.getValue().getDate() + " - "
            + caseNote.getValue().getAuthor();
    }

    /**
     * Populates the case note to be edited based on the selected option.
     * @param caseData the case data
     */
    public void populateEditCaseNote(CaseData caseData) {
        switch (caseData.getEditOrDeleteCaseNote()) {
            case "Delete" -> {
                // do nothing as the case note will be deleted in the 'about to submit' callback
            }
            case "Edit" -> {
                if (isEmpty(caseData.getCaseNoteDynamicList())) {
                    throw new IllegalArgumentException("Selected note cannot be null or empty");
                }
                String selectedCaseNoteId = caseData.getCaseNoteDynamicList().getValue().getCode();
                caseData.getCaseNotesCollection().stream()
                    .filter(caseNote -> caseNote.getId().equals(selectedCaseNoteId))
                    .findFirst()
                    .ifPresent(caseNote -> caseData.setAddCaseNote(caseNote.getValue()));
            }
            default -> throw new IllegalArgumentException(
                "Invalid edit or delete case note option: " + caseData.getEditOrDeleteCaseNote());
        }
    }

    /**
     * Submits the case note update based on the selected option. If the option is "Delete", the selected case note
     * will be removed from the collection. If the option is "Edit", the selected case note will be updated
     * with the new title and note values.
     * @param caseData the case data
     */
    public void submitCaseNoteUpdate(CaseData caseData) {
        switch (caseData.getEditOrDeleteCaseNote()) {
            case "Delete" -> {
                caseData.setCaseNotesCollection(caseData.getCaseNotesCollection().stream()
                    .filter(caseNote -> !caseNote.getId().equals(
                        caseData.getCaseNoteDynamicList().getValue().getCode()))
                    .toList());
                clearOldValues(caseData);
            }
            case "Edit" -> {
                String selectedCaseNoteId = caseData.getCaseNoteDynamicList().getValue().getCode();
                caseData.getCaseNotesCollection().stream()
                    .filter(caseNote -> caseNote.getId().equals(selectedCaseNoteId))
                    .findFirst()
                    .ifPresent(caseNote -> {
                        caseNote.getValue().setTitle(caseData.getAddCaseNote().getTitle());
                        caseNote.getValue().setNote(caseData.getAddCaseNote().getNote());
                    });
                clearOldValues(caseData);
            }
            default -> throw new IllegalArgumentException(
                "Invalid edit or delete case note option: " + caseData.getEditOrDeleteCaseNote());
        }
    }

    private static void clearOldValues(CaseData caseData) {
        caseData.setCaseNoteDynamicList(null);
        caseData.setAddCaseNote(null);
        caseData.setEditOrDeleteCaseNote(null);
    }

    private GenericTypeItem<CaseNote> getCaseNoteGenericTypeItem(String userToken, CaseNote caseNote) {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
        caseNote.setDate(formatter.format(new Date()));

        try {
            UserDetails userDetails = userService.getUserDetails(userToken);
            if (isEmpty(userDetails)) {
                throw new IllegalArgumentException("User details cannot be null or empty");
            }
            caseNote.setAuthor(isNotEmpty(userDetails.getName()) ? userDetails.getName() : userDetails.getEmail());
        } catch (Exception e) {
            log.warn("Failed to retrieve user details with error: ", e);
            caseNote.setAuthor("Unknown");
        }

        GenericTypeItem<CaseNote> caseNoteListTypeItem = new GenericTypeItem<>();
        caseNoteListTypeItem.setId(String.valueOf(randomUUID()));
        caseNoteListTypeItem.setValue(caseNote);
        return caseNoteListTypeItem;
    }
}
