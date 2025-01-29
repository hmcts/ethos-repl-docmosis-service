package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDetails;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_TIME_PATTERN;

@Slf4j
public class DigitalCaseFileHelper {

    private static final String DONE = "DONE";
    private static final String FAILED = "FAILED";

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
                .filter(bundle -> List.of(DONE, FAILED).contains(bundle.value().getStitchStatus()))
                .findFirst();
        stitchedFile.ifPresent(bundle -> caseData.setDigitalCaseFile(
                createTribunalCaseFile(caseData, bundle.value())));

    }

    private static DigitalCaseFileType createTribunalCaseFile(CaseData caseData,
                                                              BundleDetails bundleDetails) {
        DigitalCaseFileType digitalCaseFile = caseData.getDigitalCaseFile();
        if (isEmpty(digitalCaseFile)) {
            digitalCaseFile = new DigitalCaseFileType();
        }
        switch (bundleDetails.getStitchStatus()) {
            case DONE -> {
                UploadedDocumentType uploadedDocumentType = getUploadedDocumentType(bundleDetails);

                digitalCaseFile.setUploadedDocument(uploadedDocumentType);
                digitalCaseFile.setStatus("DCF Generated: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
                digitalCaseFile.setError(null);
            }
            case FAILED -> {
                digitalCaseFile.setStatus(
                        "DCF Failed to generate: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
                digitalCaseFile.setError(bundleDetails.getStitchingFailureMessage());
            }
            default -> throw new IllegalStateException("Unexpected value: " + bundleDetails.getStitchStatus());
        }

        // Deprecating old field regardless of status
        digitalCaseFile.setDateGenerated(null);

        return digitalCaseFile;
    }

    @NotNull
    private static UploadedDocumentType getUploadedDocumentType(BundleDetails bundleDetails) {
        DocumentLink documentLink = bundleDetails.getStitchedDocument();
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentFilename(documentLink.documentFilename);
        uploadedDocumentType.setDocumentUrl(documentLink.documentUrl);
        uploadedDocumentType.setDocumentBinaryUrl(documentLink.documentBinaryUrl);
        return uploadedDocumentType;
    }

    public static void setUpdatingStatus(CaseData caseData) {
        if (isEmpty(caseData.getDigitalCaseFile())) {
            caseData.setDigitalCaseFile(new DigitalCaseFileType());
        }
        caseData.getDigitalCaseFile().setStatus("DCF Updating: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN));
    }

}
