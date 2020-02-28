package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentGenerationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringRunner.class)
@WebMvcTest(DocumentGenerationController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class DocumentGenerationControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String GEN_DOC_URL = "/generateDocument";
    private static final String GEN_DOC_CONFIRMATION_URL = "/generateDocumentConfirmation";
    private static final String DOC_NAME = "doc_name";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private DocumentGenerationService documentGenerationService;

    @MockBean
    private EventValidationService eventValidationService;

    @MockBean
    private VerifyTokenService verifyTokenService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private DocumentInfo documentInfo;

    private void doRequestSetUp() throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
                .getResource("/exampleV1.json").toURI()));
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        doRequestSetUp();
        documentInfo = DocumentInfo.builder().description(DOC_NAME).build();
    }

    @Test
    public void generateDocumentOk() throws Exception {
        when(documentGenerationService.processDocumentRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenReturn(documentInfo);
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(GEN_DOC_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateDocumentConfirmation() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(GEN_DOC_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateDocumentError400() throws Exception {
        mvc.perform(post(GEN_DOC_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateDocumentError500() throws Exception {
        when(documentGenerationService.processDocumentRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(GEN_DOC_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void generateDocumentOkForbidden() throws Exception {
        when(documentGenerationService.processDocumentRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenReturn(documentInfo);
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(GEN_DOC_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateDocumentConfirmationForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(GEN_DOC_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}