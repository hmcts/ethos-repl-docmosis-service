package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
class AcasServiceTest {

    @MockBean
    private DocumentManagementService documentManagementService;
    @MockBean
    private TornadoService tornadoService;
    private AcasService acasService;
    private CaseData caseData;
    private CaseDetails caseDetails;
    private RestTemplate restTemplate;
    private List<String> errors;

    private static final String ACAS_BASE_URL = "https://api-dev-acas-01.azure-api.net/ECCLUAT";
    private static final String ACAS_API_KEY = "dummyApiKey";
    private static final String AUTH_TOKEN = "authToken";
    private static final String NOT_FOUND_OBJECT = "[{\"CertificateNumber\":\"A123456/12/12\","
                                                   + "\"CertificateDocument\":\"not found\"}]";

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        restTemplate = new RestTemplate();
        acasService = new AcasService(tornadoService, restTemplate, ACAS_BASE_URL, ACAS_API_KEY);
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

        String acasCertificate = Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("acasCertificate.json")).toURI()));

        getMockServer().expect(ExpectedCount.once(), requestTo(ACAS_BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(acasCertificate));
        DocumentInfo documentInfo = DocumentInfo.builder()
                .description("ACAS Certificate - R111111/11/11")
                .url("http://test.com/documents/random-uuid")
                .markUp("<a target=\"_blank\" href=\"https://test.com/documents/random-uuid\">Document</a>")
                .build();
        when(tornadoService.createDocumentInfoFromBytes(anyString(), anyString(), anyString(), any()))
                .thenReturn(documentInfo);
    }

    @Test
    void getAcasCertificate() throws JsonProcessingException {
        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        System.out.println(errors);
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
        getMockServer().expect(ExpectedCount.once(), requestTo(ACAS_BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        assertEquals("Error retrieving ACAS Certificate", errors.get(0));
    }

    @Test
    void certificateNotFound() throws JsonProcessingException {
        getMockServer().expect(ExpectedCount.once(), requestTo(ACAS_BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(NOT_FOUND_OBJECT));
        errors = acasService.getAcasCertificate(caseDetails, AUTH_TOKEN);
        assertEquals("No ACAS Certificate found", errors.get(0));

    }

    private MockRestServiceServer getMockServer() {
        return MockRestServiceServer.createServer(restTemplate);
    }
}
