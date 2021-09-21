package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "document_management", name = "url")
public class DocumentManagementService {

    private static final String FILES_NAME = "files";
    public static final String APPLICATION_DOCX_VALUE =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final AuthTokenGenerator authTokenGenerator;

    private final CaseDocumentClientApi caseDocumentClientApi;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;
    @Value("${document_management.url}")
    private String ccdDMStoreBaseUrl;

    @Autowired
    public DocumentManagementService(AuthTokenGenerator authTokenGenerator,
                                     CaseDocumentClientApi caseDocumentClientApi) {
        this.authTokenGenerator = authTokenGenerator;
        this.caseDocumentClientApi = caseDocumentClientApi;
    }

    @Retryable(value = {DocumentManagementException.class}, backoff = @Backoff(delay = 200))
    public URI uploadDocument(String authToken, byte[] byteArray, String outputFileName, String type) {
        try {
            MultipartFile file = new InMemoryMultipartFile(FILES_NAME, outputFileName, type, byteArray);
            var response = caseDocumentClientApi.uploadDocuments(
                    authToken,
                    authTokenGenerator.generate(),
                    new DocumentUploadRequest(
                            "PUBLIC",
                            "caseTypeId",
                            "EMPLOYMENT",
                            singletonList(file)
                )
            );
            var document = response.getDocuments().stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new DocumentManagementException("Document management failed uploading file"
                                    + OUTPUT_FILE_NAME));

            log.info("Uploaded document successful");
            return URI.create(document.links.self.href);
        } catch (Exception ex) {
            log.info("Exception: " + ex.getMessage());
            throw new DocumentManagementException(String.format("Unable to upload document %s to document management",
                    outputFileName), ex);
        }
    }


    public UploadedDocument downloadFile(String authToken, String urlString) {
        ResponseEntity<Resource> responseEntity = caseDocumentClientApi.getDocumentBinary(
                authToken,
                authTokenGenerator.generate(),
                UUID.fromString(getDownloadUrl(urlString)));
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            return UploadedDocument.builder()
                    .content(responseEntity.getBody())
                    .name(Objects.requireNonNull(responseEntity.getHeaders().get("originalfilename")).get(0))
                    .contentType(Objects.requireNonNull(responseEntity.getHeaders().get(HttpHeaders.CONTENT_TYPE)).get(0))
                    .build();
        } else {
            throw new IllegalStateException("Cannot download document that is stored in CCD got "
                    + "[" + responseEntity.getStatusCode() + "] " + responseEntity.getBody());
        }
    }

    private String getDownloadUrl(String urlString) {
        var path = urlString.replace(ccdDMStoreBaseUrl+"/documents/", "");
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
