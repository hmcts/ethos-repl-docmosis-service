package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.DocumentManagementException;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.Document;
import uk.gov.hmcts.reform.document.domain.UploadResponse;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import java.net.URI;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.OUTPUT_FILE_NAME;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "document_management", name = "url")
public class DocumentManagementService {

    private static final String FILES_NAME = "files";
    private static final String APPLICATION_DOCX_VALUE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final DocumentUploadClientApi documentUploadClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final UserService userService;
    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    @Autowired
    public DocumentManagementService(DocumentUploadClientApi documentUploadClient, AuthTokenGenerator authTokenGenerator,
                                     UserService userService) {
        this.documentUploadClient = documentUploadClient;
        this.authTokenGenerator = authTokenGenerator;
        this.userService = userService;
    }

    @Retryable(value = {DocumentManagementException.class}, backoff = @Backoff(delay = 200))
    URI uploadDocument(String authToken, byte[] byteArray) {
        try {
            log.info("ccdGatewayBaseUrl: " + ccdGatewayBaseUrl);
            MultipartFile file = new InMemoryMultipartFile(FILES_NAME, OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE, byteArray);
            UploadResponse response = documentUploadClient.upload(
                    authToken,
                    authTokenGenerator.generate(),
                    userService.getUserDetails(authToken).getUid(),
                    singletonList(file)
            );
            //log.info("Response: " + response.toString());
            Document document = response.getEmbedded().getDocuments().stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new DocumentManagementException("Document management failed uploading file" + OUTPUT_FILE_NAME));

            log.info("Uploaded document successful");
            return URI.create(document.links.self.href);
        } catch (Exception ex) {
            log.info("Exception: " + ex.getMessage());
            throw new DocumentManagementException(String.format("Unable to upload document %s to document management",
                    OUTPUT_FILE_NAME), ex);
        }
    }

    String generateDownloadableURL(URI documentSelf) {
        return ccdGatewayBaseUrl + documentSelf.getRawPath() + "/binary";
    }

    String generateMarkupDocument(String documentDownloadableURL) {
        return "<a target=\"_blank\" href=\"" + documentDownloadableURL + "\">Document</a>";
    }

}
