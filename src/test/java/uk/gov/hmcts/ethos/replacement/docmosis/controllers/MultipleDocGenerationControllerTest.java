package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleDocGenerationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleLetterService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleScheduleService;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringRunner.class)
@WebMvcTest(MultipleDocGenerationController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class MultipleDocGenerationControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String PRINT_SCHEDULE_URL = "/printSchedule";
    private static final String PRINT_LETTER_URL = "/printLetter";
    private static final String PRINT_DOCUMENT_CONFIRMATION_URL = "/printDocumentConfirmation";
    private static final String MID_SELECTED_ADDRESS_LABELS_MULTIPLE_URL = "/midSelectedAddressLabelsMultiple";
    private static final String MID_VALIDATE_ADDRESS_LABELS_MULTIPLE_URL = "/midValidateAddressLabelsMultiple";
    private static final String DYNAMIC_MULTIPLE_LETTERS = "/dynamicMultipleLetters";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private MultipleLetterService multipleLetterService;

    @MockBean
    private MultipleScheduleService multipleScheduleService;

    @MockBean
    private VerifyTokenService verifyTokenService;

    @MockBean
    private MultipleDocGenerationService multipleDocGenerationService;

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
        documentInfo = new DocumentInfo();
        documentInfo.setMarkUp("<a target=\"_blank\" href=\"null/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary\">Document</a>");
    }

    @Test
    public void printSchedule() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        when(multipleScheduleService.bulkScheduleLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class))).thenReturn(documentInfo);
        mvc.perform(post(PRINT_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void printLetter() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        when(multipleLetterService.bulkLetterLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class),
                isA(List.class), isA(Boolean.class))).thenReturn(documentInfo);
        mvc.perform(post(PRINT_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void printDocumentConfirmation() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(PRINT_DOCUMENT_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void midSelectedAddressLabelsMultiple() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(MID_SELECTED_ADDRESS_LABELS_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void midValidateAddressLabelsMultiple() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(MID_VALIDATE_ADDRESS_LABELS_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void printScheduleError400() throws Exception {
        mvc.perform(post(PRINT_SCHEDULE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void printLetterError400() throws Exception {
        mvc.perform(post(PRINT_LETTER_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void printDocumentConfirmationError400() throws Exception {
        mvc.perform(post(PRINT_DOCUMENT_CONFIRMATION_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void midSelectedAddressLabelsMultiple400() throws Exception {
        mvc.perform(post(MID_SELECTED_ADDRESS_LABELS_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void midValidateAddressLabelsMultiple400() throws Exception {
        mvc.perform(post(MID_VALIDATE_ADDRESS_LABELS_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void printScheduleError500() throws Exception {
        doThrow(new InternalException(ERROR_MESSAGE)).when(multipleScheduleService).bulkScheduleLogic(
                eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(PRINT_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void printLetterError500() throws Exception {
        when(multipleLetterService.bulkLetterLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class),
                isA(List.class), isA(Boolean.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(PRINT_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void printScheduleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(PRINT_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void printLetterForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(PRINT_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void printDocumentConfirmationForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(PRINT_DOCUMENT_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void midSelectedAddressLabelsMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(MID_SELECTED_ADDRESS_LABELS_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void midValidateAddressLabelsMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(MID_VALIDATE_ADDRESS_LABELS_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void dynamicMultipleLetters() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(DYNAMIC_MULTIPLE_LETTERS)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void dynamicMultipleLettersForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(DYNAMIC_MULTIPLE_LETTERS)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}