package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.AcasCertificate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AcasServiceTest {

    @MockBean
    private DocumentManagementService documentManagementService;
    @MockBean
    private TornadoService tornadoService;
    private AcasService acasService;
    private CaseData caseData;
    private CaseDetails caseDetails;
    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;
    private List<String> errors;

    private static final String ACAS_API_KEY = "dummyApiKey";
    private static final String AUTH_TOKEN = "authToken";
    private static final String NOT_FOUND_OBJECT = "[{\"CertificateNumber\":\"A123456/12/12\","
                                                   + "\"CertificateDocument\":\"not found\"}]";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize MockWebServer for WebClient testing
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Create WebClient pointing to a mock server
        WebClient webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build();
        String baseUrl = mockWebServer.url("/").toString();

        acasService = new AcasService(tornadoService, webClient, baseUrl, ACAS_API_KEY);
        objectMapper = new ObjectMapper();
        errors = new ArrayList<>();

        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Test");
        respondentSumType.setRespondentACAS("R111111/11/11");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData = new CaseData();
        caseData.setRespondentCollection(List.of(respondentSumTypeItem));
        caseData.setAcasCertificate("R111111/11/11");

        caseDetails = new CaseDetails();
        caseDetails.setCaseTypeId("Leeds");
        caseDetails.setCaseData(caseData);

        DocumentInfo documentInfo = DocumentInfo.builder()
                .description("ACAS Certificate - R111111/11/11")
                .url("http://test.com/documents/random-uuid")
                .markUp("<a target=\"_blank\" href=\"https://test.com/documents/random-uuid\">Document</a>")
                .build();
        when(tornadoService.createDocumentInfoFromBytes(anyString(), anyString(), anyString(), any()))
                .thenReturn(documentInfo);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void getAcasCertificate() throws JsonProcessingException {
        // Prepare mock response with valid ACAS certificate
        AcasCertificate acasCertificate = new AcasCertificate();
        acasCertificate.setCertificateNumber("R111111/11/11");
        acasCertificate.setCertificateDocument("dGVzdCBwZGYgZGF0YQ=="); // Base64 encoded "test pdf data"

        List<AcasCertificate> certificates = List.of(acasCertificate);

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(objectMapper.writeValueAsString(certificates)));

        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        assertEquals(0, errors.size());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    void nullOrEmptyAcasCert(String certifcateNumber) throws JsonProcessingException {
        caseData.setAcasCertificate(certifcateNumber);
        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        assertEquals(1, errors.size());
    }

    @Test
    void unauthorisedResponseFromAcas() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        assertEquals("Error retrieving ACAS Certificate", errors.getFirst());
    }

    @Test
    void certificateNotFound() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(NOT_FOUND_OBJECT));
        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        assertEquals("No ACAS Certificate found", errors.getFirst());

    }

}
