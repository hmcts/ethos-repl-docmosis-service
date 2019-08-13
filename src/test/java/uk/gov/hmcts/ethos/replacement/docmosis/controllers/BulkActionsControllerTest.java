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
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringRunner.class)
@WebMvcTest(BulkActionsController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class BulkActionsControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String CREATION_BULK_URL = "/createBulk";
    private static final String SEARCH_BULK_URL = "/searchBulk";
    private static final String UPDATE_BULK_URL = "/updateBulk";
    private static final String UPDATE_BULK_CASE_URL = "/updateBulkCase";
    private static final String GENERATE_BULK_LETTER_URL = "/generateBulkLetter";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private BulkCreationService bulkCreationService;

    @MockBean
    private BulkUpdateService bulkUpdateService;

    @MockBean
    private BulkSearchService bulkSearchService;

    @MockBean
    private DocumentGenerationService documentGenerationService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private BulkCasesPayload bulkCasesPayload;
    private BulkRequestPayload bulkRequestPayload;
    private BulkDetails bulkDetails;
    private List<DocumentInfo> documentInfoList;

    private void doRequestSetUp() throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
                .getResource("/exampleBulkV1.json").toURI()));
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        doRequestSetUp();
        List<SubmitEvent> submitEvents;
        List<MultipleTypeItem> multipleTypeItems = new ArrayList<>();
        bulkCasesPayload = new BulkCasesPayload();
        submitEvents = getSubmitEvents();
        bulkCasesPayload.setSubmitEvents(submitEvents);
        bulkCasesPayload.setMultipleTypeItems(multipleTypeItems);
        BulkData bulkData = new BulkData();
        bulkDetails = new BulkDetails();
        bulkDetails.setCaseData(bulkData);
        bulkRequestPayload = new BulkRequestPayload();
        bulkRequestPayload.setBulkDetails(bulkDetails);
        DocumentInfo documentInfo1 = new DocumentInfo();
        documentInfo1.setMarkUp("markup1");
        DocumentInfo documentInfo2 = new DocumentInfo();
        documentInfo2.setMarkUp("markup2");
        documentInfoList = new ArrayList<>(Arrays.asList(documentInfo1, documentInfo2));
    }

    @Test
    public void createBulkCase() throws Exception {
        when(bulkSearchService.bulkCasesRetrievalRequest(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenReturn(bulkCasesPayload);
        when(bulkCreationService.bulkCreationLogic(isA(BulkDetails.class), isA(BulkCasesPayload.class), eq(AUTH_TOKEN))).
                thenReturn(bulkRequestPayload);
        when(bulkCreationService.updateLeadCase(isA(BulkRequestPayload.class), eq(AUTH_TOKEN))).thenReturn(bulkRequestPayload);
        mvc.perform(post(CREATION_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void createBulkCaseError400() throws Exception {
        mvc.perform(post(CREATION_BULK_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBulkCaseError500() throws Exception {
        when(bulkSearchService.bulkCasesRetrievalRequest(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        mvc.perform(post(CREATION_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createSearchBulkCase() throws Exception {
        when(bulkSearchService.bulkSearchLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        mvc.perform(post(SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void createSearchBulkCaseError400() throws Exception {
        mvc.perform(post(SEARCH_BULK_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createSearchBulkCaseError500() throws Exception {
        when(bulkSearchService.bulkSearchLogic(isA(BulkDetails.class))).thenThrow(feignError());
        mvc.perform(post(SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBulk() throws Exception {
        when(bulkUpdateService.bulkUpdateLogic(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenReturn(bulkRequestPayload);
        when(bulkCreationService.updateLeadCase(isA(BulkRequestPayload.class), eq(AUTH_TOKEN))).thenReturn(bulkRequestPayload);
        when(bulkUpdateService.clearUpFields(isA(BulkRequestPayload.class))).thenReturn(bulkRequestPayload);
        mvc.perform(post(UPDATE_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateBulkError400() throws Exception {
        mvc.perform(post(UPDATE_BULK_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBulkError500() throws Exception {
        when(bulkUpdateService.bulkUpdateLogic(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        mvc.perform(post(UPDATE_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBulkCase() throws Exception {
        when(bulkCreationService.bulkUpdateCaseIdsLogic(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenReturn(bulkRequestPayload);
        when(bulkCreationService.updateLeadCase(isA(BulkRequestPayload.class), eq(AUTH_TOKEN))).thenReturn(bulkRequestPayload);
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateBulkCaseError400() throws Exception {
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBulkCaseError500() throws Exception {
        when(bulkCreationService.bulkUpdateCaseIdsLogic(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    private List<SubmitEvent> getSubmitEvents() {
        CaseData caseData = new CaseData();
        caseData.setClerkResponsible("JuanFran");
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        caseData.setRespondentSumType(respondentSumType);
        caseData.setFileLocation("Manchester");
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(caseData);
        SubmitEvent submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseData(caseData);
        return new ArrayList<>(Arrays.asList(submitEvent1, submitEvent2));
    }

    @Test
    public void generateBulkLetter() throws Exception {
        when(documentGenerationService.processBulkDocumentRequest(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenReturn(documentInfoList);
        mvc.perform(post(GENERATE_BULK_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkLetterWithErrors() throws Exception {
        when(documentGenerationService.processBulkDocumentRequest(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenReturn(new ArrayList<>());
        mvc.perform(post(GENERATE_BULK_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkLetterError400() throws Exception {
        mvc.perform(post(GENERATE_BULK_LETTER_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateBulkLetterError500() throws Exception {
        when(documentGenerationService.processBulkDocumentRequest(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        mvc.perform(post(GENERATE_BULK_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}