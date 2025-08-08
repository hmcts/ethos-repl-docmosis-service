package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.GenericTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CaseNote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
