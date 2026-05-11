package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseNotesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.JsonMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;

@ExtendWith(SpringExtension.class)
@WebMvcTest({CaseNotesController.class, JsonMapper.class})
class CaseNotesControllerTest {
    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String ABOUT_TO_SUBMIT_URL = "/caseNotes/aboutToSubmit";
    private static final String MANAGE_ABOUT_TO_START_URL = "/caseNotes/manageCaseNote/aboutToStart";
    private static final String MANAGE_MID_EVENT_URL = "/caseNotes/manageCaseNote/midEvent";
    private static final String MANAGE_ABOUT_TO_SUBMIT_URL = "/caseNotes/manageCaseNote/aboutToSubmit";

    @MockitoBean
    private CaseNotesService caseNotesService;
    @MockitoBean
    private VerifyTokenService verifyTokenService;
    @Autowired
    private JsonMapper jsonMapper;
    private CCDRequest ccdRequest;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CaseDataBuilder caseDetailsBuilder = new CaseDataBuilder();
        CaseDetails caseDetails = caseDetailsBuilder
            .withEthosCaseReference("0123456/2021")
            .buildAsCaseDetails(ACCEPTED_STATE, LEEDS_CASE_TYPE_ID);

        ccdRequest = new CCDRequest();
        ccdRequest.setCaseDetails(caseDetails);
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
    }

    @Test
    void aboutToSubmitSinglesCaseNotes() throws Exception {
        mockMvc.perform(post(ABOUT_TO_SUBMIT_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.DATA, notNullValue()))
            .andExpect(jsonPath(JsonMapper.WARNINGS, nullValue()));
    }

    @Test
    void aboutToSubmitBadToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mockMvc.perform(post(ABOUT_TO_SUBMIT_URL)
            .content(jsonMapper.toJson(ccdRequest))
            .header("Authorization", AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void manageCaseNoteAboutToStart_success() throws Exception {
        when(caseNotesService.populateCaseNoteList(any())).thenReturn(List.of());
        mockMvc.perform(post(MANAGE_ABOUT_TO_START_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.DATA, notNullValue()))
            .andExpect(jsonPath(JsonMapper.ERRORS, hasSize(0)));
    }

    @Test
    void manageCaseNoteAboutToStart_withErrors() throws Exception {
        when(caseNotesService.populateCaseNoteList(any()))
            .thenReturn(List.of("There are no telephone notes to edit or delete"));
        mockMvc.perform(post(MANAGE_ABOUT_TO_START_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.ERRORS, hasSize(1)));
    }

    @Test
    void manageCaseNoteAboutToStart_badToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mockMvc.perform(post(MANAGE_ABOUT_TO_START_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void manageCaseNoteMidEvent_success() throws Exception {
        mockMvc.perform(post(MANAGE_MID_EVENT_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.DATA, notNullValue()))
            .andExpect(jsonPath(JsonMapper.ERRORS, nullValue()));
    }

    @Test
    void manageCaseNoteMidEvent_badToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mockMvc.perform(post(MANAGE_MID_EVENT_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void manageCaseNoteAboutToSubmit_success() throws Exception {
        mockMvc.perform(post(MANAGE_ABOUT_TO_SUBMIT_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.DATA, notNullValue()))
            .andExpect(jsonPath(JsonMapper.ERRORS, nullValue()));
    }

    @Test
    void manageCaseNoteAboutToSubmit_badToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        mockMvc.perform(post(MANAGE_ABOUT_TO_SUBMIT_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
