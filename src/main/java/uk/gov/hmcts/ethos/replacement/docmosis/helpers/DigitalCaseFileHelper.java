package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDetails;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.ObjectUtils.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_TIME_PATTERN;

@Slf4j
public class DigitalCaseFileHelper {

    private static final String DONE = "DONE";
    private static final String UPLOAD = "Upload";
    private static final String REMOVE = "Remove";

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
        stitchedFile.ifPresent(bundle -> caseData.setDigitalCaseFile(createTribunalCaseFile(bundle.value())));

    }

    private static DigitalCaseFileType createTribunalCaseFile(BundleDetails bundleDetails) {
        DigitalCaseFileType digitalCaseFile = new DigitalCaseFileType();
        if (DONE.equals(bundleDetails.getStitchStatus())) {
            DocumentLink documentLink = bundleDetails.getStitchedDocument();
            UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
            uploadedDocumentType.setDocumentFilename(documentLink.documentFilename);
            uploadedDocumentType.setDocumentUrl(documentLink.documentUrl);
            uploadedDocumentType.setDocumentBinaryUrl(documentLink.documentBinaryUrl);

            digitalCaseFile.setUploadedDocument(uploadedDocumentType);
            digitalCaseFile.setStatus("DCF Updated: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
            digitalCaseFile.setError(null);
        } else {
            digitalCaseFile.setStatus("DCF Failed to generate: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
            digitalCaseFile.setError(bundleDetails.getStitchingFailureMessage());
        }

        // Deprecating old field regardless of status
        digitalCaseFile.setDateGenerated(null);

        return digitalCaseFile;
    }

    public static void setUpdatingStatus(CaseData caseData) {
        if (isEmpty(caseData.getDigitalCaseFile())) {
            caseData.setDigitalCaseFile(new DigitalCaseFileType());
        }
        caseData.getDigitalCaseFile().setStatus("DCF Updating: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
    }

    public static void uploadOrRemoveDcf(CaseData caseData) {
        switch (caseData.getUploadOrRemoveDcf()) {
            case UPLOAD -> {
                DigitalCaseFileType digitalCaseFile = caseData.getDigitalCaseFile();
                if (isNotEmpty(digitalCaseFile)) {
                    digitalCaseFile.setStatus("DCF Uploaded: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
                    digitalCaseFile.setError(null);

                    // Deprecating old field
                    digitalCaseFile.setDateGenerated(null);
                }
            }
            case REMOVE -> caseData.setDigitalCaseFile(null);
            default -> log.error("Invalid uploadOrRemoveDcf value: {}", caseData.getUploadOrRemoveDcf());
        }
    }
}
