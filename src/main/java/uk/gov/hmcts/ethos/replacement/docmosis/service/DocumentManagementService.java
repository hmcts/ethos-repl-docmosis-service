package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.model.ccd.UploadedDocument;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.Classification;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;

@Service
@Slf4j
@ComponentScan("uk.gov.hmcts.reform.ccd.document.am.feign")
public class DocumentManagementService {

    private static final String FILES_NAME = "files";
    public static final String APPLICATION_DOCX_VALUE =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String JURISDICTION = "EMPLOYMENT";
    private final DocumentUploadClientApi documentUploadClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final DocumentDownloadClientApi documentDownloadClientApi;
    private final UserService userService;
    private final CaseDocumentClient caseDocumentClient;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;
    @Value("${document_management.ccdCaseDocument.url}")
    private String ccdDMStoreBaseUrl;
    @Value("${feature.secure-doc-store.enabled}")
    private boolean secureDocStoreEnabled;

    @Autowired
    public DocumentManagementService(DocumentUploadClientApi documentUploadClient,
                                     AuthTokenGenerator authTokenGenerator, UserService userService,
                                     DocumentDownloadClientApi documentDownloadClientApi,
                                     CaseDocumentClient caseDocumentClient) {
        this.documentUploadClient = documentUploadClient;
        this.authTokenGenerator = authTokenGenerator;
        this.userService = userService;
        this.documentDownloadClientApi = documentDownloadClientApi;
        this.caseDocumentClient = caseDocumentClient;
    }

    @Retryable(value = {DocumentManagementException.class}, backoff = @Backoff(delay = 200))
    public URI uploadDocument(String authToken, byte[] byteArray, String outputFileName, String type,
                              String caseTypeID) {
        try {
            MultipartFile file = new InMemoryMultipartFile(FILES_NAME, outputFileName, type, byteArray);
            if (secureDocStoreEnabled) {
                log.info("Using Case Document Client");
                var response = caseDocumentClient.uploadDocuments(
                        authToken,
                        authTokenGenerator.generate(),
                        caseTypeID,
                        JURISDICTION,
                        singletonList(file),
                        Classification.PUBLIC
                );

                var document = response.getDocuments().stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new DocumentManagementException("Document management failed uploading file"
                                        + OUTPUT_FILE_NAME));
                log.info("Uploaded document successful");
                return URI.create(document.links.self.href);
            } else {
                log.info("Using Document Upload Client");
                var user = userService.getUserDetails(authToken);
                var response = documentUploadClient.upload(
                       authToken,
                       authTokenGenerator.generate(),
                        user.getUid(),
                        new ArrayList<>(singletonList("caseworker-employment")),
                        uk.gov.hmcts.reform.document.domain.Classification.PUBLIC,
                        singletonList(file)
                );
                var document = response.getEmbedded().getDocuments().stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new DocumentManagementException("Document management failed uploading file"
                                    + OUTPUT_FILE_NAME));
                log.info("Uploaded document successful");
                return URI.create(document.links.self.href);
            }
        } catch (Exception ex) {
            log.info("Exception: " + ex.getMessage());
            throw new DocumentManagementException(String.format("Unable to upload document %s to document management",
                    outputFileName), ex);
        }
    }

    public String generateDownloadableURL(URI documentSelf) {
        return ccdGatewayBaseUrl + documentSelf.getRawPath() + "/binary";
    }

    public String generateMarkupDocument(String documentDownloadableURL) {
        return "<a target=\"_blank\" href=\"" + documentDownloadableURL + "\">Document</a>";
    }

    public UploadedDocument downloadFile(String authToken, String urlString) {
        var user = userService.getUserDetails(authToken);
        ResponseEntity<Resource> response;

        response = documentDownloadClientApi.downloadBinary(
                authToken,
                authTokenGenerator.generate(),
                String.join(",", user.getRoles()),
                user.getUid(),
                getDownloadUrl(urlString)
        );

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return UploadedDocument.builder()
                    .content(response.getBody())
                    .name(Objects.requireNonNull(response.getHeaders().get("originalfilename")).get(0))
                    .contentType(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.CONTENT_TYPE)).get(0))
                    .build();
        } else {
            throw new IllegalStateException("Cannot download document that is stored in CCD got "
                    + "[" + response.getStatusCode() + "] " + response.getBody());
        }
    }

    private String getDownloadUrl(String urlString) {
        var path = urlString.replace(ccdDMStoreBaseUrl, "");
        if (path.startsWith("/")) {
            return path;
        }

        return "/" + path;
    }

    public String getDocumentUUID(String urlString) {
        var documentUUID = urlString.replace(ccdDMStoreBaseUrl + "/documents/", "");
        documentUUID = documentUUID.replace("/binary", "");
        return documentUUID;
    }
}
