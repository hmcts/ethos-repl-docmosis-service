package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.UploadedDocument;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService.APPLICATION_DOCX_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ACAS_CERTIFICATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.CLAIM_ACCEPTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.CLAIM_REJECTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET1;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET1_ATTACHMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET3;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET3_ATTACHMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.HEARINGS;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.LEGACY_DOCUMENT_NAMES;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.NOTICE_OF_A_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.NOTICE_OF_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.NOTICE_OF_HEARING;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.OTHER;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.REJECTION_OF_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.RESPONSE_TO_A_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.STARTING_A_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.TRIBUNAL_CORRESPONDENCE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader.successfulDocStoreUpload;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader.successfulDocumentManagementUploadResponse;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader.unsuccessfulDocumentManagementUploadResponse;

@ExtendWith(SpringExtension.class)
class DocumentManagementServiceTest {

    @Mock
    private DocumentUploadClientApi documentUploadClient;
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private UserService userService;
    @Mock
    private DocumentDownloadClientApi documentDownloadClientApi;
    @Mock
    private CaseDocumentClient caseDocumentClient;
    @InjectMocks
    private DocumentManagementService documentManagementService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File file;
    private String markup;
    private ResponseEntity<Resource> responseEntity;

    @BeforeEach
    public void setUp() {
        file = createTestFile();
        markup = "<a target=\"_blank\" href=\"null/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary\">Document</a>";
        when(authTokenGenerator.generate()).thenReturn("authString");
        responseEntity = MultipleUtil.getResponseOK();
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        ReflectionTestUtils.setField(documentManagementService, "ccdDMStoreBaseUrl", "http://dm-store:8080");
        ReflectionTestUtils.setField(documentManagementService, "secureDocStoreEnabled", false);
    }

    @Test
    void shouldUploadToDocumentManagement() throws IOException, URISyntaxException {
        when(documentUploadClient.upload(anyString(), anyString(), anyString(), anyList(), any(), anyList()))
                .thenReturn(successfulDocumentManagementUploadResponse());
        URI documentSelfPath = documentManagementService.uploadDocument("authString", Files.readAllBytes(file.toPath()),
                OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE, anyString());
        String documentDownloadableURL = documentManagementService.generateDownloadableURL(documentSelfPath);
        assertEquals(documentManagementService.generateMarkupDocument(documentDownloadableURL), markup);
        assertNotNull(documentSelfPath);
        assertEquals("/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4", documentSelfPath.getPath());
    }

    @Test
    void uploadDocumentToDocumentManagementThrowsException() throws IOException, URISyntaxException {
        when(documentUploadClient.upload(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(unsuccessfulDocumentManagementUploadResponse());
        assertThrows(DocumentManagementException.class, () ->
                documentManagementService.uploadDocument("authString", Files.readAllBytes(file.toPath()),
                OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE, anyString()));
    }

    private File createTestFile() {
        Path path = Paths.get(OUTPUT_FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("Hello World !!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.toFile();
    }

    @Test
    void downloadFile() {
        when(documentDownloadClientApi.downloadBinary(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(responseEntity);

        UploadedDocument uploadedDocument = documentManagementService.downloadFile("authString",
                "http://dm-store:8080/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary");
        assertEquals("fileName", uploadedDocument.getName());
        assertEquals("xslx", uploadedDocument.getContentType());

        uploadedDocument = documentManagementService.downloadFile("authString",
                "documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary");
        assertEquals("fileName", uploadedDocument.getName());
        assertEquals("xslx", uploadedDocument.getContentType());
    }

    @Test
    void downloadFileException() {
        when(documentDownloadClientApi.downloadBinary(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_GATEWAY));
        assertThrows(IllegalStateException.class, () ->
                documentManagementService.downloadFile("authString",
                        "documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary")
        );
    }

    @Test
    void getDocumentUUID() {
        var urlString = "http://dm-store:8080/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary";
        assertEquals("85d97996-22a5-40d7-882e-3a382c8ae1b4", documentManagementService.getDocumentUUID(urlString));
    }

    @Test
    void downloadFileSecureDocStoreTrue() {
        ReflectionTestUtils.setField(documentManagementService, "secureDocStoreEnabled", true);
        when(documentDownloadClientApi.downloadBinary(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(responseEntity);
        UploadedDocument uploadedDocument = documentManagementService.downloadFile("authString",
                "http://dm-store:8080/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary");
        assertEquals("fileName", uploadedDocument.getName());
        assertEquals("xslx", uploadedDocument.getContentType());

        uploadedDocument = documentManagementService.downloadFile("authString",
                "documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary");
        assertEquals("fileName", uploadedDocument.getName());
        assertEquals("xslx", uploadedDocument.getContentType());
    }

    @Test
    void uploadFileSecureDocStoreTrue() throws URISyntaxException, IOException {
        ReflectionTestUtils.setField(documentManagementService, "secureDocStoreEnabled", true);
        when(caseDocumentClient.uploadDocuments(anyString(), anyString(), anyString(), anyString(), anyList(), any()))
                .thenReturn(successfulDocStoreUpload());
        URI documentSelfPath = documentManagementService.uploadDocument("authString",
                Files.readAllBytes(file.toPath()), OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE, "LondonSouth");
        String documentDownloadableURL = documentManagementService.generateDownloadableURL(documentSelfPath);
        assertEquals(documentManagementService.generateMarkupDocument(documentDownloadableURL), markup);
        assertNotNull(documentSelfPath);
        assertEquals("/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4", documentSelfPath.getPath());
    }

    @ParameterizedTest
    @MethodSource
    void convertLegacyDocsToNewDocNaming(String docType, String topLevel) {
        CaseData caseData = new CaseDataBuilder()
                .withDocumentCollection(docType)
                .build();
        documentManagementService.convertLegacyDocsToNewDocNaming(caseData);
        assertNotNull(caseData.getDocumentCollection());
        assertEquals(topLevel, caseData.getDocumentCollection().get(0).getValue().getTopLevelDocuments());
    }

    private static Stream<Arguments> convertLegacyDocsToNewDocNaming() {
        return Stream.of(
                Arguments.of(ET1, STARTING_A_CLAIM),
                Arguments.of(ET1_ATTACHMENT, STARTING_A_CLAIM),
                Arguments.of(ACAS_CERTIFICATE, STARTING_A_CLAIM),
                Arguments.of(NOTICE_OF_A_CLAIM, STARTING_A_CLAIM),
                Arguments.of(TRIBUNAL_CORRESPONDENCE, STARTING_A_CLAIM),
                Arguments.of(REJECTION_OF_CLAIM, STARTING_A_CLAIM),
                Arguments.of(ET3, RESPONSE_TO_A_CLAIM),
                Arguments.of(ET3_ATTACHMENT, RESPONSE_TO_A_CLAIM),
                Arguments.of(NOTICE_OF_HEARING, HEARINGS),
                Arguments.of(OTHER, LEGACY_DOCUMENT_NAMES)
        );
    }

    @ParameterizedTest
    @MethodSource
    void setDocumentTypeForDocumentCollection(String typeOfDocument, String documentType) {
        CaseData caseData = new CaseDataBuilder()
                .withDocumentCollection(typeOfDocument)
                .build();
        documentManagementService.convertLegacyDocsToNewDocNaming(caseData);
        documentManagementService.setDocumentTypeForDocumentCollection(caseData);
        assertNotNull(caseData.getDocumentCollection());
        assertEquals(documentType, caseData.getDocumentCollection().get(0).getValue().getDocumentType());
    }

    private static Stream<Arguments> setDocumentTypeForDocumentCollection() {
        return Stream.of(
                Arguments.of(ET1, ET1),
                Arguments.of(ET1_ATTACHMENT, ET1_ATTACHMENT),
                Arguments.of(ACAS_CERTIFICATE, ACAS_CERTIFICATE),
                Arguments.of(NOTICE_OF_A_CLAIM, NOTICE_OF_CLAIM),
                Arguments.of(TRIBUNAL_CORRESPONDENCE, CLAIM_ACCEPTED),
                Arguments.of(REJECTION_OF_CLAIM, CLAIM_REJECTED),
                Arguments.of(ET3, ET3),
                Arguments.of(ET3_ATTACHMENT, ET3_ATTACHMENT),
                Arguments.of(NOTICE_OF_HEARING, NOTICE_OF_HEARING),
                Arguments.of(OTHER, OTHER)

        );
    }

}