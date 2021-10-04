package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.Classification;
import uk.gov.hmcts.reform.document.domain.UploadResponse;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "document_management", name = "url")
@ComponentScan("uk.gov.hmcts.reform.ccd.document.am.feign")
public class DocumentManagementService {

    private static final String FILES_NAME = "files";
    public static final String APPLICATION_DOCX_VALUE =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final AuthTokenGenerator authTokenGenerator;
    private final DocumentUploadClientApi documentUploadClient;
    private final UserService userService;
    private final DocumentDownloadClientApi documentDownloadClientApi;
    private final CaseDocumentClientApi caseDocumentClientApi;

    private final CaseDocumentClient caseDocumentClient;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;
    @Value("${document_management.url}")
    private String ccdDMStoreBaseUrl;
    @Value("$case-document-am.url")
    private String caseDocumentAmUrl;

    @Autowired
    public DocumentManagementService(AuthTokenGenerator authTokenGenerator,
                                     CaseDocumentClientApi caseDocumentClientApi,
                                     DocumentUploadClientApi documentUploadClient,
                                     UserService userService,
                                     DocumentDownloadClientApi documentDownloadClientApi, CaseDocumentClient caseDocumentClient) {
        this.authTokenGenerator = authTokenGenerator;
        this.caseDocumentClientApi = caseDocumentClientApi;
        this.documentUploadClient = documentUploadClient;
        this.userService = userService;
        this.documentDownloadClientApi = documentDownloadClientApi;
        this.caseDocumentClient = caseDocumentClient;
    }

    @Retryable(value = {DocumentManagementException.class}, backoff = @Backoff(delay = 200))
    public URI uploadDocument(String authToken, byte[] byteArray, String outputFileName, String type) {
        try {
            MultipartFile file = new InMemoryMultipartFile(FILES_NAME, outputFileName, type, byteArray);
            var user = userService.getUserDetails(authToken);
            UploadResponse response = documentUploadClient.upload(
                    authToken,
                    authTokenGenerator.generate(),
                    user.getUid(),
                    new ArrayList<>(singletonList("caseworker-employment")),
                    Classification.PUBLIC,
                    singletonList(file)
            );
            var document = response.getEmbedded().getDocuments().stream()
                    .findFirst()

                    .orElseThrow(() ->
                            new DocumentManagementException("Document management failed uploading file"
                                    + OUTPUT_FILE_NAME));

            log.info("Uploaded document successful");
            for(int i = 0; i<10; i++){
                log.info("-------------------------");
            }
            return URI.create(document.links.self.href);
        } catch (Exception ex) {
            log.info("Exception: " + ex.getMessage());
            throw new DocumentManagementException(String.format("Unable to upload document %s to document management",
                    outputFileName), ex);
        }

//        try {
//            MultipartFile file = new InMemoryMultipartFile(FILES_NAME, outputFileName, type, byteArray);
//            var response = caseDocumentClient.uploadDocuments(
//                    authToken,
//                    authTokenGenerator.generate(),
//                    "Leeds_Multiple",
//                    "Employment",
//                    singletonList(file),
//                    Classification.valueOf("PUBLIC")
//            );
//            var document = response.getDocuments().stream()
//                    .findFirst()
//                    .orElseThrow(() ->
//                            new DocumentManagementException("Document management failed uploading file"
//                                    + OUTPUT_FILE_NAME));
//
//            log.info("Uploaded document successful");
//            return URI.create(document.links.self.href);
//        } catch (Exception ex) {
//            log.info("Exception: " + ex.getMessage());
//            throw new DocumentManagementException(String.format("Unable to upload document %s to document management",
//                    outputFileName), ex);
//        }


    }


    public UploadedDocument downloadFile(String authToken, String urlString) {
//        ResponseEntity<Resource> responseEntity = caseDocumentClientApi.getDocumentBinary(
//                authToken,
//                authTokenGenerator.generate(),
//                UUID.fromString(getDownloadUrl(urlString)));
//        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
//            for(int i = 0; i<10; i++){
//                log.info("----Response OK-----");
//            }
//            return UploadedDocument.builder()
//                    .content(responseEntity.getBody())
//                    .name(Objects.requireNonNull(responseEntity.getHeaders().get("originalfilename")).get(0))
//                    .contentType(Objects.requireNonNull(responseEntity.getHeaders().get(HttpHeaders.CONTENT_TYPE)).get(0))
//                    .build();
//        } else {
//            throw new IllegalStateException("Cannot download document that is stored in CCD got "
//                    + "[" + responseEntity.getStatusCode() + "] " + responseEntity.getBody());
//        }

        var user = userService.getUserDetails(authToken);
        ResponseEntity<Resource> response = documentDownloadClientApi.downloadBinary(
                authToken,
                authTokenGenerator.generate(),
                String.join(",", user.getRoles()),
                user.getUid(),
                getDownloadUrl(urlString)
        );
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            for(int i = 0; i<10; i++){
                log.info("----Response OK-----");
            }
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
        log.info(path);
        if (path.startsWith("/")) {
            return path;
        }

        return "/" + path;
    }
    public String generateDownloadableURL(URI documentSelf) {
        return ccdGatewayBaseUrl + documentSelf.getRawPath() + "/binary";
    }

    public String generateMarkupDocument(String documentDownloadableURL) {
        return "<a target=\"_blank\" href=\"" + documentDownloadableURL + "\">Document</a>";
    }
}
