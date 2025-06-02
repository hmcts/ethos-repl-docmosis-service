package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.internal.mapping.JsonbMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AcasService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.JsonMapper;

import java.util.ArrayList;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({AcasCertificateController.class, JsonbMapper.class})
@ContextConfiguration(classes = DocmosisApplication.class)
class AcasCertificateControllerTest {
    private static final String RETRIEVE_ACAS_CERT_URL = "/acasCertificate/retrieveCertificate";
    private static final String ACAS_CONFIRMATION_URL = "/acasCertificate/confirmation";
    private static final String AUTH_TOKEN = "some-token";
    private CCDRequest ccdRequest;

    @MockBean
    private VerifyTokenService verifyTokenService;
    @MockBean
    private AcasService acasService;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext applicationContext;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() throws Exception {
        jsonMapper = new JsonMapper(new ObjectMapper());
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        CaseData caseData = new CaseData();
        caseData.setAcasCertificate("R111111/11/11");
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Leeds");
        ccdRequest = new CCDRequest();
        ccdRequest.setCaseDetails(caseDetails);
    }

    @Test
    void retrieveAcasCert_Success() throws Exception {

        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        when(acasService.getAcasCertificate(any(), anyString())).thenReturn(new ArrayList<>());
        mockMvc.perform(post(RETRIEVE_ACAS_CERT_URL)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                        .content(jsonMapper.toJson(ccdRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
        verify(acasService, times(1)).getAcasCertificate(any(), anyString());
    }

    @Test
    void retrieveAcasCert_invalidToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mockMvc.perform(post(RETRIEVE_ACAS_CERT_URL)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                        .content(jsonMapper.toJson(ccdRequest)))
                .andExpect(status().isForbidden());
        verify(acasService, never()).getAcasCertificate(any(), anyString());
    }

    @Test
    void retrieveAcasCert_BadRequest() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mockMvc.perform(post(RETRIEVE_ACAS_CERT_URL)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                        .content("error"))
                .andExpect(status().isBadRequest());
        verify(acasService, never()).getAcasCertificate(any(), anyString());
    }

    @Test
    void acasConfirmation_Success() throws Exception {

        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        when(acasService.getAcasCertificate(any(), anyString())).thenReturn(new ArrayList<>());
        mockMvc.perform(post(ACAS_CONFIRMATION_URL)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                        .content(jsonMapper.toJson(ccdRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    void acasConfirmation_invalidToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mockMvc.perform(post(ACAS_CONFIRMATION_URL)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                        .content(jsonMapper.toJson(ccdRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void acasConfirmation_BadRequest() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mockMvc.perform(post(ACAS_CONFIRMATION_URL)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                        .content("error"))
                .andExpect(status().isBadRequest());
    }
}