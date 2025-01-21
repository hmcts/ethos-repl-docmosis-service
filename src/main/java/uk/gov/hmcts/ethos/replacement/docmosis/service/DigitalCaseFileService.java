package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateRequest;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateResponse;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDetails;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDocument;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDocumentDetails;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DocumentType;
import uk.gov.hmcts.ethos.replacement.docmosis.client.BundleApiClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DigitalCaseFileHelper.setUpdatingStatus;

@RequiredArgsConstructor
@Service
@Slf4j
public class DigitalCaseFileService {
    private final AuthTokenGenerator authTokenGenerator;
    private final BundleApiClient bundleApiClient;
    private static final String CREATE = "Create";
    private static final String UPLOAD = "Upload";
    private static final String REMOVE = "Remove";

    public void createUploadRemoveDcf(String userToken, CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getCaseData();
        switch (caseData.getUploadOrRemoveDcf()) {
            case CREATE -> {
                caseData.setCaseBundles(createBundleData(caseData));
                stitchCaseFileAsync(userToken, caseDetails);
                setUpdatingStatus(caseData);
            }
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
        caseData.setUploadOrRemoveDcf(null);
    }

    private void stitchCaseFileAsync(String authorization, CaseDetails caseDetails) {
        bundleApiClient.asyncStitchBundle(authorization, authTokenGenerator.generate(),
                bundleRequestMapper(caseDetails));
    }

    private BundleCreateRequest bundleRequestMapper(CaseDetails caseDetails) {
        return BundleCreateRequest.builder()
                .caseDetails(caseDetails)
                .caseTypeId(caseDetails.getCaseTypeId())
                .build();
    }

    public List<Bundle> createBundleData(CaseData caseData) {
        Bundle bundle = Bundle.builder()
                .value(createBundleDetails(caseData))
                .build();
        return List.of(bundle);
    }

    private BundleDetails createBundleDetails(CaseData caseData) {
        List<BundleDocumentDetails> caseDocs = getDocsForDcf(caseData);
        List<BundleDocument> bundleDocuments = caseDocs.stream()
                .map(bundleDocumentDetails -> BundleDocument.builder()
                        .value(bundleDocumentDetails)
                        .build())
                .toList();

        return BundleDetails.builder()
                .id(UUID.randomUUID().toString())
                .title("ET - DCF")
                .eligibleForStitching(YES)
                .eligibleForCloning(NO)
                .fileName(caseData.getEthosCaseReference().replace("/", "-") + "-DCF")
                .hasTableOfContents(NO)
                .pageNumberFormat("numberOfPages")
                .documents(bundleDocuments)
                .build();
    }

    private List<BundleDocumentDetails> getDocsForDcf(CaseData caseData) {
        return caseData.getDocumentCollection().stream()
                .map(DocumentTypeItem::getValue)
                .filter(doc -> doc.getUploadedDocument() != null && isNotExcludedFromDcf(doc))
                .map(doc -> BundleDocumentDetails.builder()
                        .name(getDocumentName(doc))
                        .sourceDocument(DocumentLink.builder()
                                .documentUrl(doc.getUploadedDocument().getDocumentUrl())
                                .documentBinaryUrl(doc.getUploadedDocument().getDocumentBinaryUrl())
                                .documentFilename(doc.getUploadedDocument().getDocumentFilename())
                                .build())
                        .build())
                .toList();
    }

    private static String getDocumentName(DocumentType doc) {
        String docType = isNullOrEmpty(doc.getDocumentType())
                ? ""
                : " - " + doc.getDocumentType();
        String docFileName = isNullOrEmpty(doc.getUploadedDocument().getDocumentFilename())
                ? ""
                : " - " + doc.getUploadedDocument().getDocumentFilename();
        String docDate = isNullOrEmpty(doc.getDateOfCorrespondence())
                ? ""
                : " - " + LocalDate.parse(doc.getDateOfCorrespondence())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String customDocName = doc.getDocNumber() + docType + docFileName + docDate;
        return customDocName.length() > 250
                ? doc.getUploadedDocument().getDocumentFilename()
                : customDocName;
    }

    private static boolean isNotExcludedFromDcf(DocumentType doc) {
        return CollectionUtils.isEmpty(doc.getExcludeFromDcf()) || !YES.equals(doc.getExcludeFromDcf().get(0));
    }

}

