package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateRequest;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateResponse;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DocumentType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ethos.replacement.docmosis.client.BundleApiClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RequiredArgsConstructor
@Service
@Slf4j
public class DigitalCaseFileService {

    private final BundleApiClient bundleApiClient;
    private final AuthTokenGenerator authTokenGenerator;

    @Value("${bundle.config.default}")
    private String defaultBundle;

    /**
     * Creates a digital case file.
     * @param caseDetails case details
     * @param userToken auth token
     * @return list of bundles
     */
    public List<Bundle> createDigitalCaseFile(CaseDetails caseDetails, String userToken) {
        setBundleConfig(caseDetails.getCaseData());
        BundleCreateResponse bundleCreateResponse = createBundle(userToken, authTokenGenerator.generate(),
                bundleRequestMapper(caseDetails));
        setCustomBundleValues(caseDetails, bundleCreateResponse);
        return bundleCreateResponse.getData().getCaseBundles();
    }

    /**
     * Stitch the digital case file.
     * @param caseDetails case details
     * @param userToken auth token
     */
    public void stitchDigitalCaseFile(CaseDetails caseDetails, String userToken) {
        BundleCreateResponse bundleCreateResponse = stitchBundle(userToken, authTokenGenerator.generate(),
                bundleRequestMapper(caseDetails));
        caseDetails.getCaseData().setCaseBundles(bundleCreateResponse.getData().getCaseBundles());
        addDocumentToDcf(caseDetails.getCaseData());
    }

    private void addDocumentToDcf(CaseData caseData) {
        Optional<Bundle> stitchedFile = caseData.getCaseBundles().stream()
                .filter(bundle -> bundle.value().getStitchedDocument() != null)
                .findFirst();
        if (stitchedFile.isEmpty()) {
            log.warn("No stitched file found for case {}", caseData.getEthosCaseReference());
            return;
        }

        DocumentLink documentLink = stitchedFile.get().value().getStitchedDocument();
        caseData.setDigitalCaseFile(createTribunalCaseFile(documentLink));
    }

    private DigitalCaseFileType createTribunalCaseFile(DocumentLink documentLink) {
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentFilename(documentLink.documentFilename);
        uploadedDocumentType.setDocumentUrl(documentLink.documentUrl);
        uploadedDocumentType.setDocumentBinaryUrl(documentLink.documentBinaryUrl);

        DigitalCaseFileType digitalCaseFile = new DigitalCaseFileType();
        digitalCaseFile.setUploadedDocument(uploadedDocumentType);
        digitalCaseFile.setDateGenerated(String.valueOf(java.time.LocalDate.now()));

        return digitalCaseFile;
    }

    private void setCustomBundleValues(CaseDetails caseDetails, BundleCreateResponse bundleCreateResponse) {
        for (Bundle bundle : bundleCreateResponse.getData().getCaseBundles()) {
            bundle.value().setEligibleForStitching(YES);
            bundle.value().setFileName(
                    caseDetails.getCaseData().getEthosCaseReference().replace("/", "-") + "-DCF");
        }
    }

    private BundleCreateRequest bundleRequestMapper(CaseDetails caseDetails) {
        return BundleCreateRequest.builder()
                .caseTypeId(caseDetails.getCaseTypeId())
                .caseDetails(caseDetails)
                .jurisdictionId(caseDetails.getJurisdiction())
                .build();
    }

    private BundleCreateResponse createBundle(String userToken, String authToken,
                                              BundleCreateRequest bundleCreateRequest) {
        return bundleApiClient.newBundle(userToken, authToken, bundleCreateRequest);
    }

    /**
     * Set the bundle configuration.
     * @param caseData case data
     */
    public void setBundleConfig(CaseData caseData) {
        if (isNullOrEmpty(caseData.getBundleConfiguration())) {
            caseData.setBundleConfiguration(defaultBundle);
        }
    }

    private BundleCreateResponse stitchBundle(String userToken, String generate,
                                              BundleCreateRequest bundleCreateRequest) {
        return bundleApiClient.stitchCcdBundles(userToken, generate, bundleCreateRequest);
    }
}
