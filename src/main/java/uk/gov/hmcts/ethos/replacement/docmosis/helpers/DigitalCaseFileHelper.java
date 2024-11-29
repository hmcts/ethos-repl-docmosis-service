package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDetails;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_TIME_PATTERN;

@Slf4j
public class DigitalCaseFileHelper {

    private static final List<String> OK_STATUS = List.of("DONE", "COMPLETED");

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
        DocumentLink documentLink = bundleDetails.getStitchedDocument();
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentFilename(documentLink.documentFilename);
        uploadedDocumentType.setDocumentUrl(documentLink.documentUrl);
        uploadedDocumentType.setDocumentBinaryUrl(documentLink.documentBinaryUrl);

        DigitalCaseFileType digitalCaseFile = new DigitalCaseFileType();
        digitalCaseFile.setUploadedDocument(uploadedDocumentType);
        if (OK_STATUS.contains(defaultIfEmpty(bundleDetails.getStitchStatus(), ""))) {
            log.info(bundleDetails.getStitchStatus());
            digitalCaseFile.setStatus("DCF Generated: " + LocalDate.now().format(NEW_DATE_PATTERN));
            digitalCaseFile.setError(null);
        } else {
            digitalCaseFile.setStatus("DCF Failed to generate: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
            digitalCaseFile.setError(bundleDetails.getStitchingFailureMessage());
        }

        // Deprecating old field regardless of status
        digitalCaseFile.setDateGenerated(null);

        return digitalCaseFile;
    }
}
