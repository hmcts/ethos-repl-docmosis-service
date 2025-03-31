package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.et.common.model.ccd.CCDRequest;
import uk.gov.hmcts.et.common.model.ccd.CaseData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MigrateToReformServiceTest {

    @InjectMocks
    private MigrateToReformService migrateToReformService;
    @Mock
    private CcdClient ccdClient;
    @Captor
    private ArgumentCaptor<uk.gov.hmcts.et.common.model.ccd.CaseDetails> caseDetailsArgumentCaptor;

    private CaseDetails ecmCaseDetails;
    private static final String AUTH_TOKEN = "authToken";
    private static final String EMPLOYMENT = "EMPLOYMENT";

    @BeforeEach
    void setUp() throws Exception {
        String ccdGatewayBaseUrl = "https://manage-case.test.platform.hmcts.net";
        ReflectionTestUtils.setField(migrateToReformService, "ccdGatewayBaseUrl", ccdGatewayBaseUrl);
        ecmCaseDetails = generateCaseDetails("migrateEcmToReformLeeds-ECM.json");

        var ecmSubmitEvent = new SubmitEvent();
        ecmSubmitEvent.setCaseData(ecmCaseDetails.getCaseData());
        ecmSubmitEvent.setCaseId(Long.parseLong(ecmCaseDetails.getCaseId()));
        ecmSubmitEvent.setState(ecmCaseDetails.getState());
        when(ccdClient.retrieveCase(AUTH_TOKEN, ecmCaseDetails.getCaseTypeId(), EMPLOYMENT, ecmCaseDetails.getCaseId()))
                .thenReturn(ecmSubmitEvent);

        var reformCCDRequest = new CCDRequest();
        reformCCDRequest.setToken(new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build().generate(100));

        when(ccdClient.startCaseMigrationToReform(AUTH_TOKEN, EMPLOYMENT, ecmCaseDetails.getCaseTypeId()))
                .thenReturn(reformCCDRequest);

        var reformSubmitEvent = new uk.gov.hmcts.et.common.model.ccd.SubmitEvent();
        var reformCaseDetails = generateReformCaseDetails("migrateEcmToReformLeeds-RET.json");
        reformSubmitEvent.setCaseData(reformCaseDetails.getCaseData());
        reformSubmitEvent.setCaseId(Long.parseLong(reformCaseDetails.getCaseId()));
        reformSubmitEvent.setState(reformCaseDetails.getState());
        when(ccdClient.submitCaseCaseReform(anyString(), any(), any()))
                .thenReturn(reformSubmitEvent);

    }

    @Test
    void migrateToReform() throws IOException {
        assertDoesNotThrow(() -> migrateToReformService.migrateToReform(AUTH_TOKEN, ecmCaseDetails));
        verify(ccdClient, times(1))
                .retrieveCase(AUTH_TOKEN, ecmCaseDetails.getCaseTypeId(), EMPLOYMENT, ecmCaseDetails.getCaseId());
        verify(ccdClient, times(1))
                .startCaseMigrationToReform(AUTH_TOKEN, EMPLOYMENT, "ET_EnglandWales");
        verify(ccdClient, times(1)).submitCaseCaseReform(eq(AUTH_TOKEN), any(), any());
    }

    @Test
    @Disabled
    void verifyDocumentCollectionDocTypes() throws IOException {
        ecmCaseDetails.getCaseData().getDocumentCollection().forEach(d -> d.getValue().setDocumentType(null));
        assertDoesNotThrow(() -> migrateToReformService.migrateToReform(AUTH_TOKEN, ecmCaseDetails));
        verify(ccdClient, times(1)).submitCaseCaseReform(eq(AUTH_TOKEN),
                caseDetailsArgumentCaptor.capture(), any());
        CaseData reformCaseData = caseDetailsArgumentCaptor.getValue().getCaseData();
        reformCaseData.getDocumentCollection().stream()
                .map(d -> d.getValue().getDocumentType())
                .forEach(Assertions::assertNotNull);
        assertEquals("Needs updating", reformCaseData.getDocumentCollection().get(0).getValue().getDocumentType());
    }

    @Test
    void unknownCaseType() throws IOException {
        ecmCaseDetails.setCaseTypeId("should throw error");
        assertThrows(IllegalArgumentException.class, () ->
                migrateToReformService.migrateToReform(AUTH_TOKEN, ecmCaseDetails));
        verify(ccdClient, times(0))
                .retrieveCase(AUTH_TOKEN, ecmCaseDetails.getCaseTypeId(), EMPLOYMENT, ecmCaseDetails.getCaseId());
        verify(ccdClient, times(0))
                .startCaseMigrationToReform(AUTH_TOKEN, EMPLOYMENT, "ET_EnglandWales");
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    private uk.gov.hmcts.et.common.model.ccd.CaseDetails generateReformCaseDetails(String jsonFileName)
            throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, uk.gov.hmcts.et.common.model.ccd.CaseDetails.class);
    }

}