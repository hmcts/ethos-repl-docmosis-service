package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
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
import uk.gov.hmcts.ecm.common.model.ccd.types.DocumentType;
import uk.gov.hmcts.ethos.replacement.docmosis.client.BundleApiClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RequiredArgsConstructor
@Service
public class DigitalCaseFileService {
    private final AuthTokenGenerator authTokenGenerator;
    private final BundleApiClient bundleApiClient;
    private static final String DOCUMENT_INDEX_NAME = "%s - %s - %s";

    @Value("${em-ccd-orchestrator.config.default}")
    private String defaultBundle;

    /**
     * Creates a request to create a case file.
     * @param caseData data
     * @return list of bundles
     */
    public List<Bundle> createCaseFileRequest(CaseData caseData) {
        setBundleConfig(caseData);
        return createBundleData(caseData);
    }

    /**
     * Creates a case file.
     * @param caseDetails data
     * @param userToken token
     * @return list of bundles
     */
    public List<Bundle> stitchCaseFile(CaseDetails caseDetails, String userToken) {
        setBundleConfig(caseDetails.getCaseData());
        if (CollectionUtils.isEmpty(caseDetails.getCaseData().getCaseBundles())) {
            caseDetails.getCaseData().setCaseBundles(createBundleData(caseDetails.getCaseData()));
        }
        BundleCreateResponse bundleCreateResponse = stitchCaseFile(userToken, authTokenGenerator.generate(),
                bundleRequestMapper(caseDetails));
        return bundleCreateResponse.getData().getCaseBundles();
    }

    private BundleCreateResponse stitchCaseFile(String authorization, String serviceAuthorization,
                                                BundleCreateRequest bundleCreateRequest) {
        return bundleApiClient.stitchBundle(authorization, serviceAuthorization, bundleCreateRequest);
    }

    private BundleCreateRequest bundleRequestMapper(CaseDetails caseDetails) {
        return BundleCreateRequest.builder()
                .caseDetails(caseDetails)
                .caseTypeId(caseDetails.getCaseTypeId())
                .build();
    }

    /**
     * Sets the default bundle config is none is present.
     * @param caseData data
     */
    public void setBundleConfig(CaseData caseData) {
        if (isNullOrEmpty(caseData.getBundleConfiguration())) {
            caseData.setBundleConfiguration(defaultBundle);
        }
    }

    private List<Bundle> createBundleData(CaseData caseData) {
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
                .filter(doc -> doc.getUploadedDocument() != null && isExcludedFromDcf(doc))
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
        return doc.getDocNumber()  + docType + docFileName + docDate;
    }

    private static boolean isExcludedFromDcf(DocumentType doc) {
        return CollectionUtils.isEmpty(doc.getExcludeFromDcf()) || !YES.equals(doc.getExcludeFromDcf().get(0));
    }
}

