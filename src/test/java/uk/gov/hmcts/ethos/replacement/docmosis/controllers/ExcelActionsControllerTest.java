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
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringRunner.class)
@WebMvcTest(ExcelActionsController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class ExcelActionsControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String CREATE_BULK_EXCEL_URL = "/createBulkExcel";
    private static final String AMEND_MULTIPLE_EXCEL_URL = "/amendMultiple";
    private static final String PRE_ACCEPT_BULK_EXCEL_URL = "/preAcceptBulkExcel";
    private static final String UPDATE_BULK_CASE_EXCEL_URL = "/updateBulkCaseExcel";
    private static final String UPDATE_BULK_EXCEL_URL = "/updateBulkExcel";
    private static final String GENERATE_BULK_SCHEDULE_EXCEL_URL = "/generateBulkScheduleExcel";
    private static final String GENERATE_BULK_SCHEDULE_CONFIRMATION_EXCEL_URL = "/generateBulkScheduleConfirmationExcel";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private MultipleCreationService multipleCreationService;

    @MockBean
    private MultiplePreAcceptService multiplePreAcceptService;

    @MockBean
    private MultipleUpdateService multipleUpdateService;

    @MockBean
    private MultipleScheduleService multipleScheduleService;

    @MockBean
    private MultipleAmendCaseIdsService multipleAmendCaseIdsService;

    @MockBean
    private VerifyTokenService verifyTokenService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private DocumentInfo documentInfo;

    private void doRequestSetUp() throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
                .getResource("/exampleMultiplesV1.json").toURI()));
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        doRequestSetUp();
        documentInfo = new DocumentInfo();
        documentInfo.setMarkUp("<a target=\"_blank\" href=\"null/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary\">Document</a>");
    }

    @Test
    public void createBulkExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(CREATE_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void amendMultipleExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(AMEND_MULTIPLE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void preAcceptBulkExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(PRE_ACCEPT_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateBulkCaseExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_CASE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateBulkExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkScheduleExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        when(multipleScheduleService.bulkScheduleLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class))).thenReturn(documentInfo);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkScheduleConfirmationExcel() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_CONFIRMATION_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void createBulkExcelError400() throws Exception {
        mvc.perform(post(CREATE_BULK_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void amendMultipleExcelError400() throws Exception {
        mvc.perform(post(AMEND_MULTIPLE_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void preAcceptBulkExcelError400() throws Exception {
        mvc.perform(post(PRE_ACCEPT_BULK_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBulkCaseExcelError400() throws Exception {
        mvc.perform(post(UPDATE_BULK_CASE_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBulkExcelError400() throws Exception {
        mvc.perform(post(UPDATE_BULK_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateBulkScheduleExcelError400() throws Exception {
        mvc.perform(post(GENERATE_BULK_SCHEDULE_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateBulkScheduleConfirmationExcelError400() throws Exception {
        mvc.perform(post(GENERATE_BULK_SCHEDULE_CONFIRMATION_EXCEL_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBulkExcelError500() throws Exception {
        doThrow(feignError()).when(multipleCreationService).bulkCreationLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(CREATE_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void preAcceptBulkExcelError500() throws Exception {
        doThrow(feignError()).when(multiplePreAcceptService).bulkPreAcceptLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(PRE_ACCEPT_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBulkCaseExcelError500() throws Exception {
        doThrow(feignError()).when(multipleAmendCaseIdsService).bulkAmendCaseIdsLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_CASE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBulkExcelError500() throws Exception {
        doThrow(feignError()).when(multipleUpdateService).bulkUpdateLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void generateBulkScheduleExcelError500() throws Exception {
        doThrow(feignError()).when(multipleScheduleService).bulkScheduleLogic(eq(AUTH_TOKEN), isA(MultipleDetails.class), isA(List.class));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createBulkExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(CREATE_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void amendMultipleExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(AMEND_MULTIPLE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void preAcceptBulkExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(PRE_ACCEPT_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateBulkCaseExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(UPDATE_BULK_CASE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateBulkExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(UPDATE_BULK_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateBulkScheduleExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateBulkScheduleConfirmationExcelForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_CONFIRMATION_EXCEL_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}