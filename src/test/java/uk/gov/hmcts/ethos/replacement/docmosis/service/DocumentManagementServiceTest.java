package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsights;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.DocumentManagementException;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsights.DOCUMENT_NAME;
import static uk.gov.hmcts.ethos.replacement.docmosis.appinsights.AppInsightsEvent.DOCUMENT_MANAGEMENT_UPLOAD_FAILURE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader.successfulDocumentManagementUploadResponse;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader.unsuccessfulDocumentManagementUploadResponse;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentManagementServiceTest {

    @Mock
    private DocumentUploadClientApi documentUploadClient;
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private UserService userService;
    @Mock
    private AppInsights appInsights;
    @InjectMocks
    private DocumentManagementService documentManagementService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File file;
    private String markup;

    @Before
    public void setUp() {
        file = createTestFile();
        markup = "<a target=\"_blank\" href=\"http://localhost:3453/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary\">Document</a>";
        when(authTokenGenerator.generate()).thenReturn("authString");
        documentManagementService = new DocumentManagementService(documentUploadClient, authTokenGenerator, userService, appInsights);
    }

    @Test
    public void shouldUploadToDocumentManagement() throws IOException, URISyntaxException {
        UserDetails userDetails = new UserDetails("id", "mail@mail.com",
                "userFirstName", "userLastName", Collections.singletonList("role"));
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(documentUploadClient.upload(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(successfulDocumentManagementUploadResponse());
        URI documentSelfPath = documentManagementService.uploadDocument("authString", file);
        assertEquals(documentManagementService.generateMarkupDocument(documentSelfPath), markup);
        assertNotNull(documentSelfPath);
        assertEquals("/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4", documentSelfPath.getPath());
    }

    @Test
    public void uploadDocumentToDocumentManagementThrowsException() throws IOException, URISyntaxException {
        expectedException.expect(DocumentManagementException.class);
        expectedException.expectMessage("Unable to upload document example.docx to document management");
        UserDetails userDetails = new UserDetails("id", "mail@mail.com",
                "userFirstName", "userLastName", Collections.singletonList("role"));
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(documentUploadClient.upload(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(unsuccessfulDocumentManagementUploadResponse());
        documentManagementService.uploadDocument("authString", file);
        verify(appInsights).trackEvent(DOCUMENT_MANAGEMENT_UPLOAD_FAILURE, DOCUMENT_NAME, anyString());

    }

    private File createTestFile() {
        Path path = Paths.get("example.docx");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("Hello World !!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.toFile();
    }
}