package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;

import java.time.LocalDate;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class DigitalCaseFileHelper {

    private DigitalCaseFileHelper() {
        // access through static methods
    }

    /**
     * Add the stitched document to the digital case file field.
     * @param caseData data
     */
    public static void addDcfToDocumentCollection(CaseData caseData) {
        Optional<Bundle> stitchedFile = emptyIfNull(caseData.getCaseBundles())
                .stream()
                .filter(bundle -> bundle.value().getStitchedDocument() != null)
                .findFirst();
        if (stitchedFile.isEmpty()) {
            return;
        }

        DocumentLink documentLink = stitchedFile.get().value().getStitchedDocument();
        caseData.setDigitalCaseFile(createTribunalCaseFile(documentLink));
    }

    private static DigitalCaseFileType createTribunalCaseFile(DocumentLink documentLink) {
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentFilename(documentLink.documentFilename);
        uploadedDocumentType.setDocumentUrl(documentLink.documentUrl);
        uploadedDocumentType.setDocumentBinaryUrl(documentLink.documentBinaryUrl);
        DigitalCaseFileType digitalCaseFile = new DigitalCaseFileType();
        digitalCaseFile.setUploadedDocument(uploadedDocumentType);
        digitalCaseFile.setDateGenerated(String.valueOf(LocalDate.now()));

        return digitalCaseFile;
    }
}
