package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsights;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.DocumentManagementException;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.Document;
import uk.gov.hmcts.reform.document.domain.UploadResponse;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import java.io.File;
import java.net.URI;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsights.DOCUMENT_NAME;
import static uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsightsEvent.DOCUMENT_MANAGEMENT_UPLOAD_FAILURE;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "document_management", name = "url")
public class DocumentManagementService {

    private static final String FILES_NAME = "files";
    private static final String APPLICATION_DOCX_VALUE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private DocumentUploadClientApi documentUploadClient;
    private AuthTokenGenerator authTokenGenerator;
    private UserService userService;
    private AppInsights appInsights;

    @Autowired
    public DocumentManagementService(DocumentUploadClientApi documentUploadClient, AuthTokenGenerator authTokenGenerator,
                                     UserService userService, AppInsights appInsights) {
        this.documentUploadClient = documentUploadClient;
        this.authTokenGenerator = authTokenGenerator;
        this.userService = userService;
        this.appInsights = appInsights;
    }

    URI uploadDocument(String authorisation, File doc) {
        try {
            MultipartFile file = new InMemoryMultipartFile(FILES_NAME, doc.getName(), APPLICATION_DOCX_VALUE, FileCopyUtils.copyToByteArray(doc));
            log.info("Authrosation: " + authorisation);
            String token = authTokenGenerator.generate();
            log.info("AuthTokenGenerator: " + token);
            UploadResponse response = documentUploadClient.upload(
                    authorisation,
                    token,
                    userService.getUserDetails(authorisation).getId(),
                    singletonList(file)
            );
            log.info("Not coming here: ");
            Document document = response.getEmbedded().getDocuments().stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new DocumentManagementException("Document management failed uploading file" + doc.getName()));

            log.info("Uploaded document successful");
            return URI.create(document.links.self.href);
        } catch (Exception ex) {
            appInsights.trackEvent(DOCUMENT_MANAGEMENT_UPLOAD_FAILURE, DOCUMENT_NAME, doc.getName());
            throw new DocumentManagementException(String.format("Unable to upload document %s to document management",
                    doc.getName()), ex);
        }
    }

    String generateDownloadableURL(URI documentSelf) {
        return documentSelf.getScheme() + "://localhost:3453" + documentSelf.getRawPath() + "/binary";
        //return documentSelf.getScheme() + "://" + documentSelf.getAuthority() + documentSelf.getRawPath() + "/binary";
    }

    String generateMarkupDocument(String documentDownloadableURL) {
        return "<a target=\"_blank\" href=\"" + documentDownloadableURL + "\">Document</a>";
    }

}
