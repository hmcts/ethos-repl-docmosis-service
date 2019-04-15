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
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseCreationForCaseWorkerService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseRetrievalForCaseWorkerService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseUpdateForCaseWorkerService;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

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
@WebMvcTest(CaseActionsForCaseWorkerController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class CaseActionsForCaseWorkerControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String CREATION_CASE_URL = "/createCase";
    private static final String RETRIEVE_CASE_URL = "/retrieveCase";
    private static final String RETRIEVE_CASES_URL = "/retrieveCases";
    private static final String UPDATE_CASE_URL = "/updateCase";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private CaseCreationForCaseWorkerService caseCreationForCaseWorkerService;

    @MockBean
    private CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;

    @MockBean
    private CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private SubmitEvent submitEvent;

    private void doRequestSetUp() throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
                .getResource("/exampleV1.json").toURI()));
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        doRequestSetUp();
        submitEvent = new SubmitEvent();
    }

    @Test
    public void createCase() throws Exception {
        when(caseCreationForCaseWorkerService.caseCreationRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenReturn(submitEvent);
        mvc.perform(post(CREATION_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void retrieveCase() throws Exception {
        when(caseRetrievalForCaseWorkerService.caseRetrievalRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenReturn(submitEvent);
        mvc.perform(post(RETRIEVE_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void retrieveCases() throws Exception {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(caseRetrievalForCaseWorkerService.casesRetrievalRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenReturn(submitEventList);
        mvc.perform(post(RETRIEVE_CASES_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateCase() throws Exception {
        when(caseUpdateForCaseWorkerService.caseUpdateRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenReturn(submitEvent);
        mvc.perform(post(UPDATE_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void createCaseError400() throws Exception {
        mvc.perform(post(CREATION_CASE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCaseError500() throws Exception {
        when(caseCreationForCaseWorkerService.caseCreationRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());

        mvc.perform(post(CREATION_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void retrieveCaseError400() throws Exception {
        mvc.perform(post(RETRIEVE_CASE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void retrieveCaseError500() throws Exception {
        when(caseRetrievalForCaseWorkerService.caseRetrievalRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());

        mvc.perform(post(RETRIEVE_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void retrieveCasesError400() throws Exception {
        mvc.perform(post(RETRIEVE_CASES_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void retrieveCasesError500() throws Exception {
        when(caseRetrievalForCaseWorkerService.casesRetrievalRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());

        mvc.perform(post(RETRIEVE_CASES_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateCaseError400() throws Exception {
        mvc.perform(post(UPDATE_CASE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCaseError500() throws Exception {
        when(caseUpdateForCaseWorkerService.caseUpdateRequest(isA(CCDRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());

        mvc.perform(post(UPDATE_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}