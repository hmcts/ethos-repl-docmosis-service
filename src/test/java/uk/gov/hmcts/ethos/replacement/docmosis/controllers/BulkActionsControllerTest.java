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
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDocumentInfo;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ecm.common.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.service.BulkCreationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.BulkSearchService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.BulkUpdateService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentGenerationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.MultipleReferenceService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.SubMultipleService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringRunner.class)
@WebMvcTest(BulkActionsController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class BulkActionsControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String CREATION_BULK_URL = "/createBulk";
    private static final String CREATION_BULK_ES_URL = "/createBulkES";
    private static final String SEARCH_BULK_URL = "/searchBulk";
    private static final String MID_SEARCH_BULK_URL = "/midSearchBulk";
    private static final String UPDATE_BULK_URL = "/updateBulk";
    private static final String UPDATE_BULK_CASE_URL = "/updateBulkCase";
    private static final String GENERATE_BULK_LETTER_URL = "/generateBulkLetter";
    private static final String GENERATE_BULK_LETTER_CONFIRMATION_URL = "/generateBulkLetterConfirmation";

    private static final String SUB_MULTIPLE_DYNAMIC_LIST_URL = "/subMultipleDynamicList";
    private static final String FILTER_DEFAULTED_ALL_DYNAMIC_LIST_URL = "/filterDefaultedAllDynamicList";
    private static final String FILTER_DEFAULTED_NONE_DYNAMIC_LIST_URL = "/filterDefaultedNoneDynamicList";
    private static final String MID_CREATE_SUB_MULTIPLE_URL = "/midCreateSubMultiple";
    private static final String CREATE_SUB_MULTIPLE_URL = "/createSubMultiple";
    private static final String MID_UPDATE_SUB_MULTIPLE_URL = "/midUpdateSubMultiple";
    private static final String UPDATE_SUB_MULTIPLE_URL = "/updateSubMultiple1";
    private static final String DELETE_SUB_MULTIPLE_URL = "/deleteSubMultiple";
    private static final String GENERATE_BULK_SCHEDULE_URL = "/generateBulkSchedule";
    private static final String GENERATE_BULK_SCHEDULE_CONFIRMATION_URL = "/generateBulkScheduleConfirmation";
    private static final String PRE_ACCEPT_BULK_URL = "/preAcceptBulk";
    private static final String AFTER_SUBMITTED_BULK_URL = "/afterSubmittedBulk";

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

    @MockBean
    private MultipleReferenceService multipleReferenceService;

    @MockBean
    private SubMultipleService subMultipleService;

    @MockBean
    private VerifyTokenService verifyTokenService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private BulkCasesPayload bulkCasesPayload;
    private BulkRequestPayload bulkRequestPayload;
    private BulkDocumentInfo bulkDocumentInfo;

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
        BulkDetails bulkDetails = new BulkDetails();
        bulkDetails.setCaseData(bulkData);
        bulkRequestPayload = new BulkRequestPayload();
        bulkRequestPayload.setBulkDetails(bulkDetails);
        DocumentInfo documentInfo1 = new DocumentInfo();
        documentInfo1.setMarkUp("markup1");
        DocumentInfo documentInfo2 = new DocumentInfo();
        documentInfo2.setMarkUp("markup2");
        List<DocumentInfo> documentInfoList = new ArrayList<>(Arrays.asList(documentInfo1, documentInfo2));
        bulkDocumentInfo = new BulkDocumentInfo();
        bulkDocumentInfo.setMarkUps(documentInfoList.stream().map(DocumentInfo::getMarkUp).collect(Collectors.joining(", ")));
        bulkDocumentInfo.setErrors(new ArrayList<>());
    }

    @Test
    public void createBulkCase() throws Exception {
        when(bulkSearchService.bulkCasesRetrievalRequest(isA(BulkDetails.class), eq(AUTH_TOKEN), isA(Boolean.class))).thenReturn(bulkCasesPayload);
        when(bulkCreationService.bulkCreationLogic(isA(BulkDetails.class), isA(BulkCasesPayload.class), eq(AUTH_TOKEN), isA(String.class))).
                thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
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
    public void createBulkCaseES() throws Exception {
        when(bulkSearchService.bulkCasesRetrievalRequestElasticSearch(isA(BulkDetails.class), eq(AUTH_TOKEN), isA(Boolean.class), isA(Boolean.class))).thenReturn(bulkCasesPayload);
        when(bulkCreationService.bulkCreationLogic(isA(BulkDetails.class), isA(BulkCasesPayload.class), eq(AUTH_TOKEN), isA(String.class))).
                thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(CREATION_BULK_ES_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void createSearchBulkCase() throws Exception {
        when(bulkSearchService.bulkSearchLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
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
    public void midSearchBulkCase() throws Exception {
        when(bulkSearchService.bulkMidSearchLogic(isA(BulkDetails.class), isA(Boolean.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(MID_SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateBulk() throws Exception {
        when(bulkUpdateService.bulkUpdateLogic(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenReturn(bulkRequestPayload);
        when(bulkUpdateService.clearUpFields(isA(BulkRequestPayload.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
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
    public void updateBulkCase() throws Exception {
        when(bulkCreationService.bulkUpdateCaseIdsLogic(isA(BulkRequest.class), eq(AUTH_TOKEN), isA(Boolean.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    private List<SubmitEvent> getSubmitEvents() {
        CaseData caseData = new CaseData();
        caseData.setClerkResponsible("JuanFran");
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));

        caseData.setFileLocation("Manchester");
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(caseData);
        SubmitEvent submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseData(caseData);
        return new ArrayList<>(Arrays.asList(submitEvent1, submitEvent2));
    }

    @Test
    public void generateBulkLetter() throws Exception {
        when(documentGenerationService.processBulkDocumentRequest(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenReturn(bulkDocumentInfo);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
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
    public void generateBulkLetterConfirmation() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_LETTER_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkLetterWithErrors() throws Exception {
        BulkDocumentInfo bulkDocumentInfo1 = new BulkDocumentInfo();
        bulkDocumentInfo1.setErrors(new ArrayList<>(Collections.singleton("There are not cases searched to generate letters")));
        bulkDocumentInfo1.setMarkUps("");
        when(documentGenerationService.processBulkDocumentRequest(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenReturn(bulkDocumentInfo1);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
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
    public void midCreateSubMultiple() throws Exception {
        when(bulkSearchService.bulkMidSearchLogic(isA(BulkDetails.class), isA(Boolean.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(MID_CREATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void createSubMultiple() throws Exception {
        when(subMultipleService.createSubMultipleLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(CREATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void subMultipleDynamicList() throws Exception {
        when(subMultipleService.populateSubMultipleDynamicListLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(SUB_MULTIPLE_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void filterDefaultedAllDynamicList() throws Exception {
        when(subMultipleService.populateFilterDefaultedDynamicListLogic(isA(BulkDetails.class), isA(String.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(FILTER_DEFAULTED_ALL_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void filterDefaultedNoneDynamicList() throws Exception {
        when(subMultipleService.populateFilterDefaultedDynamicListLogic(isA(BulkDetails.class), isA(String.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(FILTER_DEFAULTED_NONE_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void midUpdateSubMultiple() throws Exception {
        when(subMultipleService.bulkMidUpdateLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(MID_UPDATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateSubMultiple() throws Exception {
        when(subMultipleService.updateSubMultipleLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(UPDATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void deleteSubMultiple() throws Exception {
        when(subMultipleService.deleteSubMultipleLogic(isA(BulkDetails.class))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(DELETE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkSchedule() throws Exception {
        when(documentGenerationService.processBulkScheduleRequest(isA(BulkRequest.class), isA(String.class))).thenReturn(bulkDocumentInfo);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkScheduleConfirmation() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkScheduleWithErrors() throws Exception {
        bulkDocumentInfo.setErrors(new ArrayList<>(Collections.singleton("Error")));
        when(documentGenerationService.processBulkScheduleRequest(isA(BulkRequest.class), isA(String.class))).thenReturn(bulkDocumentInfo);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateBulkScheduleWithBulkInfo() throws Exception {
        bulkDocumentInfo.setDocumentInfo(new DocumentInfo());
        when(documentGenerationService.processBulkScheduleRequest(isA(BulkRequest.class), isA(String.class))).thenReturn(bulkDocumentInfo);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void preAcceptBulk() throws Exception {
        when(bulkSearchService.retrievalCasesForPreAcceptRequest(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenReturn(bulkCasesPayload.getSubmitEvents());
        when(bulkUpdateService.bulkPreAcceptLogic(isA(BulkDetails.class), any(), eq(AUTH_TOKEN), eq(false))).thenReturn(bulkRequestPayload);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(PRE_ACCEPT_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void afterSubmittedBulk() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(AFTER_SUBMITTED_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
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
    public void createBulkCaseESError400() throws Exception {
        mvc.perform(post(CREATION_BULK_ES_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
    public void midSearchBulkCaseError400() throws Exception {
        mvc.perform(post(MID_SEARCH_BULK_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
    public void updateBulkCaseError400() throws Exception {
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
    public void midCreateSubMultipleError400() throws Exception {
        mvc.perform(post(MID_CREATE_SUB_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void subMultipleDynamicListError400() throws Exception {
        mvc.perform(post(SUB_MULTIPLE_DYNAMIC_LIST_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createSubMultipleError400() throws Exception {
        mvc.perform(post(CREATE_SUB_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterDefaultedAllDynamicListError400() throws Exception {
        mvc.perform(post(FILTER_DEFAULTED_ALL_DYNAMIC_LIST_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterDefaultedNoneDynamicListError400() throws Exception {
        mvc.perform(post(FILTER_DEFAULTED_NONE_DYNAMIC_LIST_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void midUpdateSubMultipleError400() throws Exception {
        mvc.perform(post(MID_UPDATE_SUB_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSubMultipleError400() throws Exception {
        mvc.perform(post(UPDATE_SUB_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteSubMultipleError400() throws Exception {
        mvc.perform(post(DELETE_SUB_MULTIPLE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateBulkScheduleError400() throws Exception {
        mvc.perform(post(GENERATE_BULK_SCHEDULE_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void preAcceptBulkError400() throws Exception {
        mvc.perform(post(PRE_ACCEPT_BULK_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBulkCaseError500() throws Exception {
        when(bulkSearchService.bulkCasesRetrievalRequest(isA(BulkDetails.class), eq(AUTH_TOKEN), isA(Boolean.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(CREATION_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createBulkCaseESError500() throws Exception {
        when(bulkSearchService.bulkCasesRetrievalRequestElasticSearch(isA(BulkDetails.class), eq(AUTH_TOKEN), isA(Boolean.class), isA(Boolean.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(CREATION_BULK_ES_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createSearchBulkCaseError500() throws Exception {
        when(bulkSearchService.bulkSearchLogic(isA(BulkDetails.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void midSearchBulkCaseError500() throws Exception {
        when(bulkSearchService.bulkMidSearchLogic(isA(BulkDetails.class), isA(Boolean.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(MID_SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBulkError500() throws Exception {
        when(bulkUpdateService.bulkUpdateLogic(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBulkCaseError500() throws Exception {
        when(bulkCreationService.bulkUpdateCaseIdsLogic(isA(BulkRequest.class), eq(AUTH_TOKEN), isA(Boolean.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void generateBulkLetterError500() throws Exception {
        when(documentGenerationService.processBulkDocumentRequest(isA(BulkRequest.class), eq(AUTH_TOKEN))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void midCreateSubMultipleError500() throws Exception {
        when(bulkSearchService.bulkMidSearchLogic(isA(BulkDetails.class), isA(Boolean.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(MID_CREATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createSubMultipleError500() throws Exception {
        when(subMultipleService.createSubMultipleLogic(isA(BulkDetails.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(CREATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void subMultipleDynamicListError500() throws Exception {
        when(subMultipleService.populateSubMultipleDynamicListLogic(isA(BulkDetails.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(SUB_MULTIPLE_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void filterDefaultedAllDynamicListError500() throws Exception {
        when(subMultipleService.populateFilterDefaultedDynamicListLogic(isA(BulkDetails.class), isA(String.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(FILTER_DEFAULTED_ALL_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void filterDefaultedNoneDynamicListError500() throws Exception {
        when(subMultipleService.populateFilterDefaultedDynamicListLogic(isA(BulkDetails.class), isA(String.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(FILTER_DEFAULTED_NONE_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void midUpdateSubMultipleError500() throws Exception {
        when(subMultipleService.bulkMidUpdateLogic(isA(BulkDetails.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(MID_UPDATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateSubMultipleError500() throws Exception {
        when(subMultipleService.updateSubMultipleLogic(isA(BulkDetails.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(UPDATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteSubMultipleError500() throws Exception {
        when(subMultipleService.deleteSubMultipleLogic(isA(BulkDetails.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(DELETE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void generateBulkScheduleError500() throws Exception {
        when(documentGenerationService.processBulkScheduleRequest(isA(BulkRequest.class), isA(String.class))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void preAcceptBulkError500() throws Exception {
        when(bulkSearchService.retrievalCasesForPreAcceptRequest(isA(BulkDetails.class), eq(AUTH_TOKEN))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(PRE_ACCEPT_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createBulkCaseForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(CREATION_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createBulkCaseESForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(CREATION_BULK_ES_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createSearchBulkCaseForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void midSearchBulkCaseForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(MID_SEARCH_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateBulkForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(UPDATE_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateBulkCaseForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(UPDATE_BULK_CASE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateBulkLetterForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(GENERATE_BULK_LETTER_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateBulkLetterConfirmationForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(GENERATE_BULK_LETTER_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void midCreateSubMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(MID_CREATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createSubMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(CREATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void subMultipleDynamicListForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(SUB_MULTIPLE_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void filterDefaultedAllDynamicListForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(FILTER_DEFAULTED_ALL_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void filterDefaultedNoneDynamicListForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(FILTER_DEFAULTED_NONE_DYNAMIC_LIST_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void midUpdateSubMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(MID_UPDATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateSubMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(UPDATE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteSubMultipleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(DELETE_SUB_MULTIPLE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateBulkScheduleForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void generateBulkScheduleConfirmationForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(GENERATE_BULK_SCHEDULE_CONFIRMATION_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void preAcceptBulkForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(PRE_ACCEPT_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void afterSubmittedBulkForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mvc.perform(post(AFTER_SUBMITTED_BULK_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}